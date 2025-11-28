package com.ledger.ledger_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true) // El Email no se puede repetir
    private String email;

    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}