package org.example.paymentservice.dto.events;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
    String orderId,
    BigDecimal amount) {
}
