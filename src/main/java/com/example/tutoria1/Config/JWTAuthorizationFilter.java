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
						authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
	
	private boolean isJWTValid(HttpServletRequest request) {
		String authenticationHeader = request.getHeader(HEADER_AUTHORIZATION_KEY);
		return authenticationHeader != null && authenticationHeader.startsWith(TOKEN_BEARER_PREFIX);
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			if (isJWTValid(request)) {
				Claims claims = setSigningKey(request);
				if (claims.get("authorities") != null) {
					setAuthentication(claims);
				} else {
					SecurityContextHolder.clearContext();
				}
			} else {
				SecurityContextHolder.clearContext();
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}
}