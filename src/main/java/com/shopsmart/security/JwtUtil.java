package com.shopsmart.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	@Value("${app.jwt.secret}")
	private String secretKey;
	
	@Value("${app.jwt.expiration}")
	private long jwtExpiration;
	
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return buildToken(claims, userDetails);
	}
	
	private String buildToken(Map<String, Object> claims,
			                                        UserDetails userDetails) {
		return Jwts.builder()
				              .setClaims(claims)
				              .setSubject(userDetails.getUsername())
				              .setIssuedAt(new Date(System.currentTimeMillis()))
				              .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				              .signWith(getSigningKey(), SignatureAlgorithm.HS256)
				              .compact(); 
	}
	
	//Signing Key
	private Key getSigningKey	() {
		byte[] kyeBytes = secretKey.getBytes();
		return Keys.hmacShaKeyFor(kyeBytes);
	}

}
