package com.example.tutoria1.Config;

import static com.example.tutoria1.Config.Constans.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import io.jsonwebtoken.Jwts;

@Configuration
public class JWTAuthenticationConfig {
	
	public String getJWTToken(String username) {
		return getJWTToken(username, "ROLE_USER");
	}
	
	public String getJWTToken(String username, String authority) {
		
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList(authority);
		
		String token = Jwts
				.builder()
				.id("PPOOII_JWT")
				.subject(username)
				.claim("authorities",
						grantedAuthorities.stream()
							.map(GrantedAuthority::getAuthority)
							.collect(Collectors.toList()))
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
				.signWith(getSigningKey(SUPER_SECRET_KEY))
				.compact();
		
		return TOKEN_BEARER_PREFIX + token;
	}
}