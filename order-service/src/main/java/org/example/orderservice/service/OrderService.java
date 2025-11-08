
package org.example.orderservice.service;

import java.util.List;

import org.example.orderservice.dto.request.CreateOrderRequestDTO;
import org.example.orderservice.model.Order;

public interface OrderService {
  Order createOder(CreateOrderRequestDTO createOrderRequestDTO);

  Order getOrderById(String id);

  List<Order> getAllOrders();
}
