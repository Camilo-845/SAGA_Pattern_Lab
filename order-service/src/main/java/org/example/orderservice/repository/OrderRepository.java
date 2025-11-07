
package org.example.orderservice.repository;

import java.util.ArrayList;
import java.util.List;

import org.example.orderservice.model.Order;

public class OrderRepository {
  private List<Order> orders = new ArrayList<>();

  public Order save(Order order) {
    this.orders.add(order);
    return order;
  }
}
