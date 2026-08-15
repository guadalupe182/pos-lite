package com.Gdev.pos_lite.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {

    private final SecretKey key;

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // Usamos la clave en texto plano (la misma que está en application.properties)
        byte[] secretBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpMinutes() * 60 * 1000);
        return Jwts.builder().subject(subject).claims(claims).issuer(jwtProperties.getIssuer()).issuedAt(now).expiration(expiry).signWith(key).compact();
    }

    // Exponer la clave para que el decoder la use
    public SecretKey getKey() {
        return key;
    }
}
