package com.ledger.ledger_system.controller;

import com.ledger.ledger_system.model.Account;
import com.ledger.ledger_system.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> createAccount() {
        try {
            // Obtenemos el email del usuario logueado desde el Token
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Account createdAccount = accountService.createAccountForUser(email);
            return ResponseEntity.ok("Cuenta creada con éxito. Número: " + createdAccount.getAccountNumber());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}