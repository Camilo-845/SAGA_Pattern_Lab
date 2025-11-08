
package org.example.orderservice.service.impl;

import java.util.List;

import org.example.orderservice.config.RabbitMQConfig;
import org.example.orderservice.dto.command.ReserveInventoryCommand;
import org.example.orderservice.dto.request.CreateOrderRequestDTO;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.Status;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Override
  @Transactional
  public Order createOder(CreateOrderRequestDTO createOrderRequestDTO) {
    Order order = Order.builder()
        .productId(createOrderRequestDTO.getProductId())
        .quantity(createOrderRequestDTO.getQuantity())
        .status(Status.CREATED)
        .build();

    orderRepository.save(order);

    ReserveInventoryCommand command = new ReserveInventoryCommand(
        order.getId().toString(),
        order.getProductId(),
        order.getQuantity());

    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.INVENTORY_ROUTING_KEY, command);

    System.out.println("ReserveInventoryCommand enviado para la orden: " + order.getId());

    return order;
  }

  @Override
  public Order getOrderById(String id) {
    Order order = orderRepository.findById(id);
    return order;
  }

  @Override
  public List<Order> getAllOrders() {
    List<Order> orders = orderRepository.findAll();
    return orders;
  }

}
