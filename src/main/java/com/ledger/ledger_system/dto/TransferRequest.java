package com.ledger.ledger_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull(message = "La cuenta de origen es obligatoria")
    private Long sourceAccountId;

    @NotNull(message = "La cuenta de destino es obligatoria")
    private Long targetAccountId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero") // <--- ESTO EVITA EL HACKEO
    @Min(value = 1, message = "El monto mínimo es 1")
    private BigDecimal amount;
}