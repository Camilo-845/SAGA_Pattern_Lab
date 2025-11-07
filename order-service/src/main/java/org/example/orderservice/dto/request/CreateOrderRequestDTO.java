
package org.example.orderservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateOrderRequestDTO {
  private String productId;
  private Integer quantity;
}
