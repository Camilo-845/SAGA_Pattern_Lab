
package org.example.orderservice.service;

import org.example.orderservice.dto.request.CreateOrderRequestDTO;
import org.example.orderservice.model.Order;

public interface OrderService {
  Order createOder(CreateOrderRequestDTO createOrderRequestDTO);
}
