package com.ledger.ledger_system.security;

import com.ledger.ledger_system.repository.UserRepository; // Necesitamos verificar que el usuario exista
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User; // Ojo: Este es el User de Spring Security, no el tuyo
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtRequestFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("✅ TOKEN LEIDO. USUARIO: " + username);
            } catch (Exception e) {
                System.out.println("❌ ERROR DE FIRMA/TOKEN: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("🔎 BUSCANDO USUARIO EN DB: " + username);

            var dbUser = userRepository.findByEmail(username);

            if (dbUser == null) {
                System.out.println("⛔ USUARIO NO ENCONTRADO EN BASE DE DATOS");
            } else if (jwtUtil.validateToken(jwt)) {
                System.out.println("🔓 ACCESO CONCEDIDO A: " + username);
                // ... lógica de autenticación ...
                UserDetails userDetails = new User(dbUser.getEmail(), dbUser.getPassword(), new ArrayList<>());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("⚠️ TOKEN INVALIDO (Aunque el usuario existe)");
            }
        }

        chain.doFilter(request, response);
    }
}