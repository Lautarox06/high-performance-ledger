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

        // 1. Buscamos el header "Authorization: Bearer eyJhb..."
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Quitamos la palabra "Bearer "
            username = jwtUtil.extractUsername(jwt);
        }

        // 2. Si hay usuario y no está autenticado todavía en el sistema
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Verificamos que el usuario exista en BD
            com.ledger.ledger_system.model.User dbUser = userRepository.findByEmail(username);

            if (dbUser != null && jwtUtil.validateToken(jwt)) {

                // Creamos la sesión de seguridad temporal
                UserDetails userDetails = new User(dbUser.getEmail(), dbUser.getPassword(), new ArrayList<>());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 3. Dejar pasar la petición (al siguiente filtro o al controlador)
        chain.doFilter(request, response);
    }
}