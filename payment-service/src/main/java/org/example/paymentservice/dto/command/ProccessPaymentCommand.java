package org.example.paymentservice.dto.command;

import java.math.BigDecimal;

public record ProccessPaymentCommand(
    String orderId,
    BigDecimal amount) {
}
