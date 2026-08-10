package com.ticket_system.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	
	//----生成與驗證 Token----
	
	//密鑰 (密鑰長度需至少 256 bits / 32 個字元)
	private final String SECRET_KEY="TicketSystemSecretKeyForJwtAuthenticationTokenGeneration";
	
	//Token 有效時間: 24 小時 (毫秒)
	private final long EXPIRATION_TIME=86400000;
	
	//將回傳型態改為 SecretKey
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	}
	
	//產生 JWT Token
	public String generateToken(String username, String role) {
		return Jwts.builder()
				.subject(username)
				.claim("role", role)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
				.signWith(getSigningKey())
				.compact();
	}
	
	//----解析與驗證 Token----
	
	//從 Token 解析出 Username
	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}
	
	//從 token 解析出 Role
	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}
	
	//驗證 Token 是否有效(沒過期且簽名正確)
	public boolean validateToken(String token, String username) {
		final String extractedUsername=extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}
	
	private boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
}
