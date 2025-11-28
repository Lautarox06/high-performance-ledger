package com.ledger.ledger_system.service;

import com.ledger.ledger_system.model.Account;
import com.ledger.ledger_system.model.User;
import com.ledger.ledger_system.repository.AccountRepository;
import com.ledger.ledger_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Account createAccountForUser(String email) {
        // 1. Buscar usuario por el email del Token
        User user = userRepository.findByEmail(email);

        // 2. Validar que no tenga cuenta ya
        if (accountRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("El usuario ya tiene una cuenta registrada.");
        }

        // 3. Crear la cuenta
        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setBalance(new BigDecimal("1000.00")); // Bono de bienvenida

        // Generar número de cuenta aleatorio de 6 dígitos
        long randomNum = ThreadLocalRandom.current().nextLong(100000, 999999);
        newAccount.setAccountNumber(String.valueOf(randomNum));

        return accountRepository.save(newAccount);
    }
}