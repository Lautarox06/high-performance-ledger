package com.ledger.ledger_system;

import com.ledger.ledger_system.model.Account;
import com.ledger.ledger_system.model.User;
import com.ledger.ledger_system.repository.AccountRepository;
import com.ledger.ledger_system.repository.UserRepository;
import com.ledger.ledger_system.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testConcurrentTransfers() throws InterruptedException {
        System.out.println("--- INICIANDO PRUEBA DE HACKEO (CONCURRENCIA) ---");

        // 1. Preparar datos: Crear un usuario rico y uno pobre
        User user1 = new User();
        user1.setName("User Rico");
        user1.setEmail("rico@test.com");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("User Pobre");
        user2.setEmail("pobre@test.com");
        user2 = userRepository.save(user2);

        Account account1 = new Account();
        account1.setUser(user1);
        account1.setAccountNumber("999999");
        account1.setBalance(new BigDecimal("1000.00")); // Tiene 1000
        account1 = accountRepository.save(account1);

        Account account2 = new Account();
        account2.setUser(user2);
        account2.setAccountNumber("888888");
        account2.setBalance(new BigDecimal("0.00"));
        account2 = accountRepository.save(account2);

        // 2. Configurar el ataque: 50 hilos intentando sacar dinero a la vez
        int numberOfThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Contadores para ver qué pasa
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Long sourceId = account1.getId();
        Long targetId = account2.getId();

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // Intentamos transferir 20 (50 veces * 20 = 1000 total)
                    transferService.transferMoney(sourceId, targetId, new BigDecimal("20.00"));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Si falla (por bloqueo optimista), contamos el fallo
                    System.out.println("Transacción rechazada por seguridad: " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Esperar a que terminen todos los hilos

        // 3. Verificación final
        Account updatedAccount1 = accountRepository.findById(sourceId).orElseThrow();
        Account updatedAccount2 = accountRepository.findById(targetId).orElseThrow();

        System.out.println("--- RESULTADOS ---");
        System.out.println("Intentos exitosos: " + successCount.get());
        System.out.println("Intentos fallidos (Bloqueados): " + failCount.get());
        System.out.println("Saldo final Cuenta Origen: " + updatedAccount1.getBalance());
        System.out.println("Saldo final Cuenta Destino: " + updatedAccount2.getBalance());

        // La suma de dinero siempre debe ser 1000 (No se creó ni destruyó dinero)
        BigDecimal totalSystem = updatedAccount1.getBalance().add(updatedAccount2.getBalance());
        assertEquals(0, new BigDecimal("1000.00").compareTo(totalSystem));

        System.out.println("--- PRUEBA SUPERADA: EL DINERO ESTÁ A SALVO ---");
    }
}