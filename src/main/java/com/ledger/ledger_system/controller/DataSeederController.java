package com.ledger.ledger_system.controller;

import com.ledger.ledger_system.model.Account;
import com.ledger.ledger_system.model.User;
import com.ledger.ledger_system.repository.AccountRepository;
import com.ledger.ledger_system.repository.UserRepository;
import com.ledger.ledger_system.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/setup")
public class DataSeederController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeederController(UserRepository userRepository, AccountRepository accountRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/init")
    public ResponseEntity<?> initData() {
        Map<String, String> response = new HashMap<>();

        // 1. Crear Usuario 1 (Origen)
        User user1 = createUserIfNotExists("Juan Perez", "juan@test.com", "123456");
        Account acc1 = createAccountIfNotExists(user1, "111111", new BigDecimal("5000.00"));
        String token1 = jwtUtil.generateToken(user1.getEmail());

        // 2. Crear Usuario 2 (Destino)
        User user2 = createUserIfNotExists("Maria Gomez", "maria@test.com", "123456");
        Account acc2 = createAccountIfNotExists(user2, "222222", new BigDecimal("2000.00"));
        String token2 = jwtUtil.generateToken(user2.getEmail());

        // 3. Preparar respuesta con todo listo para usar
        response.put("mensaje", "¡Entorno de pruebas inicializado!");
        response.put("usuario_1", user1.getEmail());
        response.put("cuenta_1", acc1.getAccountNumber());
        response.put("token_juan", token1); // <--- ¡AQUÍ ESTÁ LA MAGIA!

        response.put("usuario_2", user2.getEmail());
        response.put("cuenta_2", acc2.getAccountNumber());
        response.put("token_maria", token2);

        return ResponseEntity.ok(response);
    }

    private User createUserIfNotExists(String name, String email, String password) {
        User existing = userRepository.findByEmail(email);
        if (existing != null) return existing;

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    private Account createAccountIfNotExists(User user, String number, BigDecimal balance) {
        if (accountRepository.existsByUserId(user.getId())) {
            return accountRepository.findByAccountNumber(number); // O buscar la que tenga
        }
        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(number);
        account.setBalance(balance);
        return accountRepository.save(account);
    }
}