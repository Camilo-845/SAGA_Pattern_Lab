package org.example.orderservice.dto.command;

public record ReleaseInventoryCommand(
    String orderId,
    String productId,
    int quantity) {
}
