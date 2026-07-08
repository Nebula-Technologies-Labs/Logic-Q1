package com.logicq1.crm.services.jwt;

import com.logicq1.crm.modules.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRATION =
            1000L * 60 * 15;

    private static final long REFRESH_TOKEN_EXPIRATION =
            1000L * 60 * 60 * 24 * 30;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(User user) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("type", "ACCESS");
        claims.put("userId", user.getId().toString());

        if (user.getOrganization() != null) {
            claims.put(
                    "orgId",
                    user.getOrganization().getId().toString()
            );
        }

        if (user.getRole() != null) {
            claims.put(
                    "roleId",
                    user.getRole().getId().toString()
            );

            claims.put(
                    "role",
                    user.getRole().getRole().name()
            );
        }

        return createToken(
                claims,
                user.getEmail(),
                ACCESS_TOKEN_EXPIRATION
        );
    }

    public String generateRefreshToken(User user) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("type", "REFRESH");
        claims.put("userId", user.getId().toString());

        return createToken(
                claims,
                user.getEmail(),
                REFRESH_TOKEN_EXPIRATION
        );
    }

    private String createToken(
            Map<String, Object> claims,
            String subject,
            long expiration
    ) {

        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public UUID extractUserId(String token) {
        String userId = extractAllClaims(token)
                .get("userId", String.class);

        return UUID.fromString(userId);
    }

    public String extractRole(String token) {
        return extractAllClaims(token)
                .get("role", String.class);
    }

    public String extractOrgId(String token) {
        return extractAllClaims(token)
                .get("orgId", String.class);
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token)
                .get("type", String.class);
    }

    public boolean isAccessToken(String token) {
        return "ACCESS".equals(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(extractTokenType(token));
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .before(new Date());
    }

    public boolean validateAccessToken(String token) {
        try {
            return isAccessToken(token)
                    && !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            return isRefreshToken(token)
                    && !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}