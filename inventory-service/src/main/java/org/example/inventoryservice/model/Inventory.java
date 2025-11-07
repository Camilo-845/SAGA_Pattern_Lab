
package org.example.inventoryservice.model;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class Inventory {
  private UUID id;
  private String productId;
  private Integer avaliableQuantity;
  private BigDecimal price;
}
