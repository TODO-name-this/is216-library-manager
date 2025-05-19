package com.todo.backend.utils.auth;

import com.todo.backend.entity.identity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
    // TODO: improve functions return type for better readability in case of invalid token
    @Value("${jwt.secret}")
    private String SECRET;

    public String generateAccessToken(String userId, UserRole role) {
        // 2 hours in ms
        int ttl = 2 * 60 * 60 * 1000;
        return Jwts.builder()
                .subject(userId)
                // with role claim
                .claim("role", role.name())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(String userId) {
        // 7 days in ms
        int ttl = 7 * 24 * 60 * 60 * 1000;
        return Jwts.builder()
                .subject(userId)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(getSecretKey())
                .compact();
    }

    public JwtUserInfo extractAccessToken(String token) throws JwtException {
        var userId = extractAccessClaim(token, Claims::getSubject);
        var userRole = extractAccessClaim(token, claims -> {
            String role = claims.get("role", String.class);
            return UserRole.valueOf(role);
        });
        return new JwtUserInfo(userId, userRole);
    }

    public String getUserIdRefreshToken(String token) throws JwtException {
        return extractRefreshClaim(token, Claims::getSubject);
    }

    // dont use this variable, use the getter
    private SecretKey _secretKey = null;
    private SecretKey getSecretKey() {
        // cache the key
        if (_secretKey == null) {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET);
            _secretKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return _secretKey;
    }

    private <T> T extractAccessClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAccessClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAccessClaims(String token) {
        return Jwts.parser()
                .require("type", "access")
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractRefreshClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractRefreshClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractRefreshClaims(String token) {
        return Jwts.parser()
                .require("type", "refresh")
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
