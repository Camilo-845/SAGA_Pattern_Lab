package org.example.orderservice.dto.command;

import java.math.BigDecimal;

public record ProccessPaymentCommand(
    String orderId,
    BigDecimal amount) {
}
