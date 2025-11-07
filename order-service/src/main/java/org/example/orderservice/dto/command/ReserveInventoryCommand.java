
package org.example.orderservice.dto.command;

public record ReserveInventoryCommand(
    String orderId,
    String productId,
    int quantity) {
}
