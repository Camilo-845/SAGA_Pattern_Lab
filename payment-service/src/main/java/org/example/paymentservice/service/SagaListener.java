package org.example.paymentservice.service;

import org.example.paymentservice.config.RabbitMQConfig;
import org.example.paymentservice.dto.command.ProccessPaymentCommand;
import org.example.paymentservice.dto.events.PaymentCompletedEvent;
import org.example.paymentservice.dto.events.PaymentFailedEvent;
import org.example.paymentservice.model.Payment;
import org.example.paymentservice.model.Status;
import org.example.paymentservice.repository.PaymentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SagaListener {

  @Autowired
  private PaymentRepository paymentRepository;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Transactional
  @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE_NAME)
  public void handleProccessPayment(ProccessPaymentCommand command) {
    System.out.println("ProccessPaymentCommand recibido para la orden: " + command.orderId());

    Payment payment = Payment.builder()
        .orderId(command.orderId())
        .amount(command.amount())
        .status((command.amount().doubleValue() < 600) ? Status.SUCCESS : Status.FAILED)
        .build();

    paymentRepository.save(payment);

    if (payment.getStatus() == Status.SUCCESS) {
      System.out.println("Pago procesado con exito para la orden: " + command.orderId());

      PaymentCompletedEvent event = new PaymentCompletedEvent(
          command.orderId(),
          payment.getAmount());

      rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY, event);

      System.out.println("Enviando PaymentCompletedEvent para la orden: " + command.orderId());
    } else {
      System.out.println("El pago ha fallado para la orden: " + command.orderId());

      PaymentFailedEvent event = new PaymentFailedEvent(
          command.orderId(),
          payment.getAmount(),
          "Fondos insuficientes");

      rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.PAYMENT_FAILED_ROUTING_KEY, event);
      System.out.println("Enviando PaymentFailedEvent para la orden: " + command.orderId());
    }

  }
}
