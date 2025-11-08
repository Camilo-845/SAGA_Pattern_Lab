
package org.example.inventoryservice.dto.event;

public record InventoryRejectedEvent(
    String oderId,
    String productId,
    int quantity,
    String reason) {
}
