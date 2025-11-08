
package org.example.inventoryservice.dto.command;

public record ReserveInvetoryCommand(
    String orderId,
    String productId,
    int quantity) {
}
