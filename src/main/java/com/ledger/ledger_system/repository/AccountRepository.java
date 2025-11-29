package com.ledger.ledger_system.repository;

import com.ledger.ledger_system.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 1. Para saber si el usuario ya tiene cuenta (Crear Cuenta)
    boolean existsByUserId(Long userId);

    // 2. Para ver el saldo en el Frontend (Ver Saldo)
    List<Account> findByUserId(Long userId);

    // 3. Para el Data Seeder y buscar cuentas por número (Transferencias/Admin)
    // ESTE ES EL QUE FALTABA
    Account findByAccountNumber(String accountNumber);
}