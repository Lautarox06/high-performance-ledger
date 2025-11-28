package com.ledger.ledger_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 1. La "Llave Maestra" para firmar (En prod esto va en variables de entorno)
    // Debe ser larga y segura para algoritmo HS256
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 2. Duración del token (ej: 1 día en milisegundos)
    private static final long EXPIRATION_TIME = 86400000;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // A quién pertenece el token
                .setIssuedAt(new Date()) // Cuándo se creó
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Cuándo vence
                .signWith(KEY) // Firmarlo digitalmente
                .compact();
    }

    // Validar si el token es real y devolver el usuario
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token); // Si el token está alterado o vencido, esto lanza error
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}