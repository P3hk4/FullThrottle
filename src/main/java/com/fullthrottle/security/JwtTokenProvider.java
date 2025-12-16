package com.fullthrottle.security;

import com.fullthrottle.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
        Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        User user = userDetailsImpl.getUser();
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(Long.toString(user.getId()))
                .claim("username", user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {

            Jwts.parserBuilder()
            .setSigningKey(jwtSecret).build()
            .parseClaimsJws(token);
        return true;

        } catch (JwtException | IllegalArgumentException ex) {
            logJwtError(ex);
            return false;
        }
    }

    private void logJwtError(Exception ex) {
        String errorType = "JWT error";
        if (ex instanceof MalformedJwtException) {
            errorType = "Invalid JWT token";
        } else if (ex instanceof ExpiredJwtException) {
            errorType = "Expired JWT token";
        } else if (ex instanceof UnsupportedJwtException) {
            errorType = "Unsupported JWT token";
        }
        System.err.println(errorType + ": " + ex.getMessage());
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).get("username", String.class);
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    private Claims getClaims(String token) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        
        return
        
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

    }
}