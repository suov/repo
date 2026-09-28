package com.example.tutoria1.Controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Config.JWTAuthenticationConfig;
import com.example.tutoria1.Dto.Jwt.JwtRequest;
import com.example.tutoria1.Dto.Jwt.JwtResponse;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	private final JWTAuthenticationConfig jwtAuthenticationConfig;
	private final UserDetailsService jwtInMemoryUserDetailsService;

	JwtAuthenticationController(
		JWTAuthenticationConfig jwtAuthenticationConfig, 
		UserDetailsService jwtInMemoryUserDetailsService
	) {
		this.jwtAuthenticationConfig = jwtAuthenticationConfig;
		this.jwtInMemoryUserDetailsService = jwtInMemoryUserDetailsService;
	}
	
	@RequestMapping(
			value = "/authenticate",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<?> createAuthenticationToken(
		@RequestBody JwtRequest authenticationRequest
	) throws Exception {

		if (authenticationRequest == null 
			|| authenticationRequest.getUsername() == null 
			|| authenticationRequest.getPassword() == null) {
			throw new BadCredentialsException("Credenciales inválidas");
		}

		final UserDetails userDetails = jwtInMemoryUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		if (!userDetails.getPassword().equals(authenticationRequest.getPassword())) {
			throw new BadCredentialsException("Credenciales inválidas");
		}
		
		final String authority = userDetails.getAuthorities().stream()
				.findFirst()
				.map(grantedAuthority -> grantedAuthority.getAuthority())
				.orElse("ROLE_USER");

		final String token = jwtAuthenticationConfig.getJWTToken(
			userDetails.getUsername(), 
			authority
		);
		
		return ResponseEntity.ok(new JwtResponse(token));
	}
}