package com.shopsmart.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
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
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token); 
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) { 
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder() // Create a JwtParserBuilder
				              .setSigningKey(getSigningKey()) // Set the signing key for verifying the JWT signature
				              .build() // Build the JwtParser
				              .parseClaimsJws(token) // Parse the JWT and verify its signature
				              .getBody(); // Get the claims from the parsed JWT
	}
	
	//Signing Key
	private Key getSigningKey	() {
		byte[] kyeBytes = secretKey.getBytes();
		return Keys.hmacShaKeyFor(kyeBytes);
	}

}
