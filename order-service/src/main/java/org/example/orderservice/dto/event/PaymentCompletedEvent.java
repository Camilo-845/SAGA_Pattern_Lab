package org.example.orderservice.dto.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
    String orderId,
    BigDecimal amount) {
}
