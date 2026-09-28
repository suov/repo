package com.example.tutoria1.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	@Autowired
	JWTAuthenticationConfig jwtAuthenticationConfig;
	
	@Autowired
	private UserDetailsService jwtInMemoryUserDetailsService;
	
	@RequestMapping(
			value = "/authenticate",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<?> createAuthenticationToken(
			@RequestBody JwtRequest authenticationRequest
			) throws Exception {
		
		System.out.println("**********************************");
		System.out.println("authenticationRequest.getUsername():["+authenticationRequest.getUsername()+"]");
		System.out.println("authenticationRequest.getPassword():["+authenticationRequest.getPassword()+"]");
		System.out.println("**********************************");		
		final UserDetails userDetails = jwtInMemoryUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		final String token = jwtAuthenticationConfig.getJWTToken(userDetails.getUsername());
		System.out.println("**********************************");
		System.out.println("token:["+token+"]");
		System.out.println("**********************************");
		return ResponseEntity.ok(new JwtResponse(token));
	}
}