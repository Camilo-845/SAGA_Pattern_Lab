package org.example.paymentservice.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class Payment {
  private UUID id;
  private String orderId;
  private BigDecimal amount;
  private Status status;
  private Timestamp timestamp;
}
