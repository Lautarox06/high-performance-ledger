package com.ledger.ledger_system.controller;

import com.ledger.ledger_system.dto.TransferRequest;
import com.ledger.ledger_system.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {

        try {
            transferService.transferMoney(
                    request.getSourceAccountId(),
                    request.getTargetAccountId(),
                    request.getAmount()
            );
            return ResponseEntity.ok("✅ Transferencia exitosa");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }
}