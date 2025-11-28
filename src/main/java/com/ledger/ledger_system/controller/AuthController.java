package com.ledger.ledger_system.controller;

import com.ledger.ledger_system.model.User;
import com.ledger.ledger_system.repository.UserRepository;
import com.ledger.ledger_system.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Usamos BCrypt para no guardar contraseñas en texto plano (Seguridad Básica)
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 1. REGISTRO: Crear usuario con contraseña encriptada
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // Encriptamos la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "Usuario registrado con éxito";
    }

    // 2. LOGIN: Recibir credenciales y dar Token
    @PostMapping("/login")
    public String login(@RequestBody User loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Si la contraseña coincide, generamos el Token
            return jwtUtil.generateToken(user.getEmail());
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}