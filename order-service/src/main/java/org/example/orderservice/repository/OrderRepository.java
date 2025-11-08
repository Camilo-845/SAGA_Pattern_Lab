
package org.example.orderservice.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.orderservice.model.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
  private List<Order> orders = new ArrayList<>();

  public Order save(Order order) {
    order.setId(UUID.randomUUID());
    order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
    this.orders.add(order);
    return order;
  }

  public Order findById(UUID id) {
    return orders.stream()
        .filter(order -> order.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  public Order findById(String id) {
    return orders.stream()
        .filter(order -> order.getId().toString().equals(id))
        .findFirst()
        .orElse(null);
  }

  public List<Order> findAll() {
    return orders;
  }

}
