
package org.example.orderservice.service;

import org.example.orderservice.config.RabbitMQConfig;
import org.example.orderservice.dto.command.ProccessPaymentCommand;
import org.example.orderservice.dto.command.ReleaseInventoryCommand;
import org.example.orderservice.dto.event.InventoryRejectedEvent;
import org.example.orderservice.dto.event.InventoryReserveEvent;
import org.example.orderservice.dto.event.OrderCompletedEvent;
import org.example.orderservice.dto.event.PaymentCompletedEvent;
import org.example.orderservice.dto.event.PaymentFailedEvent;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.Status;
import org.example.orderservice.repository.OrderRepository;
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

  @Transactional
  @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCESS_QUEUE_NAME)
  public void handlePaymentSuccess(PaymentCompletedEvent event) {
    System.out.println("PaymentCompletedEvent recibido para la orden: " + event.orderId());
    Order order = orderRepository.findById(event.orderId());
    order.setStatus(Status.COMPLETED);
    orderRepository.save(order);

    OrderCompletedEvent newEvent = new OrderCompletedEvent(event.orderId());
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ORDER_COMPLETED_ROUTING_KEY, newEvent);

    System.out.println("OrderCompletedEvent enviado para la orden: " + event.orderId());
  }

  @Transactional
  @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_QUEUE_NAME)
  public void handlePaymentFailed(PaymentFailedEvent event) {
    System.out.println("PaymentFailedEvent recibido para la orden: " + event.orderId() + ". Razon: " + event.reason());
    Order order = orderRepository.findById(event.orderId());
    order.setStatus(Status.CANCELLED);
    orderRepository.save(order);

    ReleaseInventoryCommand command = new ReleaseInventoryCommand(
        order.getId().toString(),
        order.getProductId(),
        order.getQuantity());

    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.INVENTORY_RELEASE_ROUTING_KEY, command);

    System.out.println("ReleaseInventoryCommand enviado para la orden: " + order.getId());
  }
}
