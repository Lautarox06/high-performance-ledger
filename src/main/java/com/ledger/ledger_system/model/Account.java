package com.ledger.ledger_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    private BigDecimal balance;

    // Esto conecta la Cuenta con el Usuario (Foreign Key)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Esto lo usaremos luego para evitar hackeos de concurrencia
    @Version
    private Integer version;
}