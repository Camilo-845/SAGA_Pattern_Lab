package org.example.paymentservice.repository;

import java.util.ArrayList;
import java.util.List;

import org.example.paymentservice.model.Payment;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {

  private List<Payment> payments = new ArrayList<>();

  public Payment save(Payment payment) {
    payment.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
    payments.add(payment);
    return payment;
  }
}
