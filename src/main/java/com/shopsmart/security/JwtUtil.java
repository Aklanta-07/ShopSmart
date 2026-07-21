package com.shopsmart.security;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	@Value("${app.jwt.secret}")
	private String secretKey;
	
	@Value("${app.jwt.expiration}")
	private long jwtExpiration;
	
	//Signing Key
	private Key getSigningKey	() {
		byte[] kyeBytes = secretKey.getBytes();
		return Keys.hmacShaKeyFor(kyeBytes);
	}

}
