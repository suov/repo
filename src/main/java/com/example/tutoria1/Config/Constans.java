package com.example.tutoria1.Config;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class Constans {

	//Spring Security
	public static final String LOGIN_URL = "/authenticate";
	public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
	public static final String HEADER_API_KEY = "X-API-KEY";
	public static final String TOKEN_BEARER_PREFIX = "Bearer ";	
	
	//JWT
	public static final String SUPER_SECRET_KEY = "EstaEsMiContraseñaSuperSecretaQueNadiePuedeSaberYNoPuedoCompartir";
	public static final long TOKEN_EXPIRATION_TIME = 864_000_000;
	
	public static Key getSigningKey864(String secret) {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public static Key getSigningKey(String secret) {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
