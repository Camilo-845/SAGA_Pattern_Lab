package org.example.orderservice.dto.event;

public record InventoryRejectedEvent(
    String orderId,
    String productId,
    int quantity,
    String reason) {
}
