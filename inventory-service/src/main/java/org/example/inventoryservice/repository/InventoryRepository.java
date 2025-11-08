
package org.example.inventoryservice.repository;

import java.util.ArrayList;
import java.util.List;

import org.example.inventoryservice.model.Inventory;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryRepository {
  private List<Inventory> inventories = new ArrayList<>();

  public Inventory save(Inventory inventory) {
    if (this.findById(inventory.getProductId()) != null) {
      this.inventories.removeIf(inv -> inv.getProductId().equals(inventory.getProductId()));
    }
    this.inventories.add(inventory);
    return inventory;
  }

  public Inventory findById(String id) {
    return inventories.stream()
        .filter(inventory -> inventory.getProductId().equals(id))
        .findFirst()
        .orElse(null);
  }

  public InventoryRepository() {
    inventories.add(Inventory.builder()
        .id(java.util.UUID.randomUUID())
        .productId("prod-1")
        .avaliableQuantity(100)
        .price(new java.math.BigDecimal("29.99"))
        .build());
    inventories.add(Inventory.builder()
        .id(java.util.UUID.randomUUID())
        .productId("prod-2")
        .avaliableQuantity(50)
        .price(new java.math.BigDecimal("49.99"))
        .build());
    inventories.add(Inventory.builder()
        .id(java.util.UUID.randomUUID())
        .productId("prod-3")
        .avaliableQuantity(200)
        .price(new java.math.BigDecimal("19.99"))
        .build());
  }

  public List<Inventory> findAll() {
    return inventories;
  }
}
