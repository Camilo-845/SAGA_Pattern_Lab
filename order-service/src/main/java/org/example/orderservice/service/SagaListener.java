
package org.example.orderservice.service;

import org.example.orderservice.config.RabbitMQConfig;
import org.example.orderservice.dto.command.ProccessPaymentCommand;
import org.example.orderservice.dto.event.InventoryRejectedEvent;
import org.example.orderservice.dto.event.InventoryReserveEvent;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.Status;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SagaListener {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Transactional
  @RabbitListener(queues = RabbitMQConfig.ORDER_RESERVED_QUEUE_NAME)
  public void handleInventoryReserved(InventoryReserveEvent event) {
    System.out.println("OrderReserved recibido para la orden: " + event.orderId());

    Order order = orderRepository.findById(event.orderId());
    order.setStatus(Status.PENDING_PAYMENT);
    order.setTotalAmount(event.totalAmount());

    orderRepository.save(order);

    ProccessPaymentCommand command = new ProccessPaymentCommand(
        order.getId().toString(),
        order.getTotalAmount());
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.PAYMENT_ROUTING_KEY, command);

    System.out.println("ProccessPaymentCommand enviado para la orden: " + order.getId());
  }

  @Transactional
  @RabbitListener(queues = RabbitMQConfig.ORDER_REJECTED_QUEUE_NAME)
  public void handleInventoryRejected(InventoryRejectedEvent event) {
    System.out.println("InventoryRejectedEvent recibido para la orden: " + event.orderId());
    Order order = orderRepository.findById(event.orderId());
    order.setStatus(Status.REJECTED);

    orderRepository.save(order);
    System.out
        .println("Saga finalizada. Orden actualizada a REJECTED: " + order.getId() + ". Razón: " + event.reason());
  }
}
