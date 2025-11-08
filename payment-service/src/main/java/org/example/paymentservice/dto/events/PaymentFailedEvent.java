package org.example.paymentservice.dto.events;

import java.math.BigDecimal;

public record PaymentFailedEvent(
    String orderId,
    BigDecimal amount,
    String reason) {
}
