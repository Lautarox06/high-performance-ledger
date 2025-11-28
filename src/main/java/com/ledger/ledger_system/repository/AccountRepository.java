package com.ledger.ledger_system.repository;

import com.ledger.ledger_system.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Spring crea automáticamente el SQL para esto:
    // SELECT * FROM accounts WHERE account_number = ?
    boolean existsByUserId(Long userId);

    Account findByAccountNumber(String accountNumber);
}