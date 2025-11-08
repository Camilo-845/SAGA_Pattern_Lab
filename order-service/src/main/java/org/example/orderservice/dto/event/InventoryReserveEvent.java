package org.example.orderservice.dto.event;

import java.math.BigDecimal;

public record InventoryReserveEvent(
    String orderId,
    String productId,
    int quantity,
    BigDecimal totalAmount) {
}
