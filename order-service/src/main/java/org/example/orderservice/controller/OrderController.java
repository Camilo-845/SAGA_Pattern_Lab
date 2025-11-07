package org.example.orderservice.controller;

import org.example.orderservice.dto.request.CreateOrderRequestDTO;
import org.example.orderservice.model.Order;
import org.example.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private OrderService orderService;

  @PostMapping("/")
  public ResponseEntity<Order> createOder(@RequestBody CreateOrderRequestDTO request) {
    Order newOrder = orderService.createOder(request);
    return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
  }

}
