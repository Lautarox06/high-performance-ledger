package com.ledger.ledger_system;

//import com.ledger.ledger_system.service.TransferService;
// import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LedgerSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LedgerSystemApplication.class, args);
    }
/*
    @Bean
    public CommandLineRunner demo(TransferService transferService) {
        return (args) -> {
            System.out.println("--- INICIANDO SISTEMA BANCARIO ---");

            // En la BD que creamos con SQL:
            // ID 1 es la cuenta de Juan (Saldo 1000)
            // ID 2 es la cuenta de Maria (Saldo 500)

            try {
                System.out.println("Intentando transferir $200 de Juan a Maria...");

                // Transferir 200.00
                transferService.transferMoney(1L, 2L, new BigDecimal("200.00"));

            } catch (Exception e) {
                System.err.println("❌ ERROR: " + e.getMessage());
            }

            System.out.println("--- OPERACION FINALIZADA ---");
        };
    }
 */
}