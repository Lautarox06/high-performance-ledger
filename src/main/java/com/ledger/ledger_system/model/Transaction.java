package com.ledger.ledger_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la cuenta origen
    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    // Relación con la cuenta destino
    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    private BigDecimal amount;

    private String type; // "TRANSFERENCIA"

    private String status; // "EXITOSA"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Un pequeño truco para guardar la fecha automáticamente antes de insertar
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}