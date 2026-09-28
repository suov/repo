package com.example.tutoria1.Config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.tutoria1.Model.UsuarioModel;
import com.example.tutoria1.repository.UsuarioRepository;

import static com.example.tutoria1.Config.Constans.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	private final UsuarioRepository usuarioRepository;

	JWTAuthorizationFilter(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	private Claims setSigningKey(HttpServletRequest request) {

		String authenticationHeader = request.getHeader(HEADER_AUTHORIZATION_KEY);

		if (authenticationHeader == null || authenticationHeader.isBlank()) {
			throw new MalformedJwtException("No hay token JWT en la cabecera Authorization");
		}

		String jwtToken = authenticationHeader.replace(TOKEN_BEARER_PREFIX, "").trim();

		return Jwts.parser()
				.verifyWith((SecretKey) getSigningKey(SUPER_SECRET_KEY))
				.build()
				.parseSignedClaims(jwtToken)
				.getPayload();
	}
	
	private void setAuthentication(Claims claims) {

		List<String> authorities = claims.get("authorities", List.class);

		if (authorities == null || authorities.isEmpty()) {
			SecurityContextHolder.clearContext();
			return;
		}

		UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
						authorities.stream()
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList()));

		SecurityContextHolder.getContext().setAuthentication(auth);
	}
	
	private boolean isJWTValid(HttpServletRequest request) {

		String authenticationHeader = request.getHeader(HEADER_AUTHORIZATION_KEY);

		return authenticationHeader != null && authenticationHeader.startsWith(TOKEN_BEARER_PREFIX);
	}
	
	private boolean isPublicRoute(HttpServletRequest request) {

		String uri = request.getRequestURI();

		return uri.equals("/authenticate")
				|| uri.startsWith("/api/vehiculos/estadoDocumento/")
				|| uri.startsWith("/api/vehiculos/placa/")
				|| uri.startsWith("/api/vehiculos/por-vencer")
				|| uri.startsWith("/api/vehiculo-persona/conductores/pueden-operar")
				|| uri.equals("/persona/total-por-tipo");
	}
	
	private boolean hasValidApiKey(HttpServletRequest request) {

		String apiKey = request.getHeader(HEADER_API_KEY);

		if (apiKey == null || apiKey.isBlank()) {
			return false;
		}

		String subject = SecurityContextHolder.getContext().getAuthentication() != null
				? SecurityContextHolder.getContext().getAuthentication().getName()
				: null;

		if (subject == null || subject.isBlank()) {
			return false;
		}

		UsuarioModel usuario = usuarioRepository.findByUsuarioPKLogin(subject).orElse(null);

		return usuario != null && apiKey.equals(usuario.getApikey());
	}
	
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, 
		HttpServletResponse response, 
		FilterChain filterChain
	) throws ServletException, IOException {

		try {
			if (isJWTValid(request)) {

				Claims claims = setSigningKey(request);

				if (claims.get("authorities") != null) {
					setAuthentication(claims);
				} else {
					SecurityContextHolder.clearContext();
				}
			} else if (!isPublicRoute(request)) {
				SecurityContextHolder.clearContext();
			}

			if (!isPublicRoute(request) && SecurityContextHolder.getContext().getAuthentication() != null && !hasValidApiKey(request)) {

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Se requiere un token JWT válido y la cabecera X-API-KEY autorizada");
				return;
			}

			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException 
			| UnsupportedJwtException 
			| MalformedJwtException e) {

			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}
}