package org.example.orderservice.controller;

import org.example.orderservice.dto.request.CreateOrderRequestDTO;
import org.example.orderservice.model.Order;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Controller
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/")
  public ResponseEntity<Order> createOder(@RequestBody CreateOrderRequestDTO request) {
    Order newOrder = orderService.createOder(request);
    return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Order> getOrderById(@PathVariable String id) {
    Order order = orderService.getOrderById(id);
    return new ResponseEntity<>(order, HttpStatus.OK);
  }

  @GetMapping("/")
  public ResponseEntity<java.util.List<Order>> getAllOrders() {
    java.util.List<Order> orders = orderService.getAllOrders();
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

}
