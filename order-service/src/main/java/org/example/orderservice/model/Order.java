package org.example.orderservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Data
@ToString
public class Order {
    private UUID id;

    private String productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Status status;
    private Timestamp createdAt;
}
