package org.example.orderservice.dto.event;

import java.math.BigDecimal;

public record PaymentFailedEvent(
    String orderId,
    BigDecimal amount,
    String reason) {
}
