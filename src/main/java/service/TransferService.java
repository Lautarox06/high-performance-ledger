package com.ledger.ledger_system.service;

import com.ledger.ledger_system.model.Account;
import com.ledger.ledger_system.model.Transaction;
import com.ledger.ledger_system.repository.AccountRepository;
import com.ledger.ledger_system.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository; // Nuevo

    // Constructor actualizado con las dos dependencias
    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transferMoney(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {

        // 1. Validaciones y Movimiento de Dinero (Igual que antes)
        Account source = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new RuntimeException("Cuenta origen no existe"));

        Account target = accountRepository.findById(targetAccountId)
                .orElseThrow(() -> new RuntimeException("Cuenta destino no existe"));

        if (source.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(target);

        // 2. GUARDAR EL HISTORIAL (AUDITORÍA) --- NUEVO
        Transaction trx = new Transaction();
        trx.setSourceAccount(source);
        trx.setTargetAccount(target);
        trx.setAmount(amount);
        trx.setType("TRANSFERENCIA");
        trx.setStatus("EXITOSA");

        transactionRepository.save(trx); // <--- Guardamos el recibo

        System.out.println("✅ TRANSFERENCIA REALIZADA Y AUDITADA ID: " + trx.getId());
    }
}