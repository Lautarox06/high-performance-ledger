package com.ledger.ledger_system.controller;

import com.ledger.ledger_system.model.Account;
import com.ledger.ledger_system.model.User;
import com.ledger.ledger_system.repository.AccountRepository;
import com.ledger.ledger_system.repository.UserRepository;
import com.ledger.ledger_system.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;      // <--- FALTABA ESTO
    private final AccountRepository accountRepository; // <--- FALTABA ESTO

    // Constructor con las 3 dependencias inyectadas
    public AccountController(AccountService accountService, UserRepository userRepository, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    // Endpoint 1: Crear Cuenta (Ya lo tenías)
    @PostMapping
    public ResponseEntity<?> createAccount() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Account createdAccount = accountService.createAccountForUser(email);
            return ResponseEntity.ok("Cuenta creada con éxito. Número: " + createdAccount.getAccountNumber());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint 2: Ver mi Saldo (ESTE ES EL QUE FALTABA)
    @GetMapping("/me")
    public ResponseEntity<?> getMyAccount() {
        System.out.println(">>> SOLICITANDO SALDO...");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());

        // Buscamos las cuentas por ID de usuario
        List<Account> cuentas = accountRepository.findByUserId(user.getId());

        System.out.println(">>> CUENTAS ENCONTRADAS: " + cuentas.size());
        return ResponseEntity.ok(cuentas);
    }
}