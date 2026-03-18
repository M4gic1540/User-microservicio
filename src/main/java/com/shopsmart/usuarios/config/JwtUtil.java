package com.shopsmart.usuarios.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidades para generar y validar tokens JWT.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(secret.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(UserDetails userDetails) {
        return generarToken(new HashMap<>(), userDetails, expiration);
    }

    public String generarRefreshToken(UserDetails userDetails) {
        return generarToken(Map.of("type", "refresh"), userDetails, refreshExpiration);
    }

    private String generarToken(Map<String, Object> claims, UserDetails userDetails, long expMs) {
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expMs))
            .signWith(getSigningKey())
            .compact();
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        final String email = extraerEmail(token);
        return email.equals(userDetails.getUsername()) && !esTokenExpirado(token);
    }

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    public long getExpiration() {
        return expiration;
    }

    public <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extraerTodosClaims(token));
    }

    private Claims extraerTodosClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private boolean esTokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }
}
