package org.example.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  public static final String EXCHANGE_NAME = "saga.exchange";
  public static final String INVENTORY_QUEUE_NAME = "inventory.queue";
  public static final String INVENTORY_ROUTING_KEY = "inventory.reserve";
  public static final String INVENTORY_RELEASE_QUEUE_NAME = "inventory.release.queue";
  public static final String INVENTORY_RELEASE_ROUTING_KEY = "inventory.release";

  public static final String ORDER_RESERVED_QUEUE_NAME = "order.reserved.queue";
  public static final String ORDER_REJECTED_QUEUE_NAME = "order.rejected.queue";
  public static final String ORDER_RESERVED_ROUTING_KEY = "order.reserved";
  public static final String ORDER_REJECTED_ROUTING_KEY = "order.rejected";
  public static final String ORDER_COMPLETED_ROUTING_KEY = "order.completed";

  public static final String PAYMENT_ROUTING_KEY = "payment.process";

  public static final String PAYMENT_SUCCESS_QUEUE_NAME = "payment.success.queue";
  public static final String PAYMENT_FAILED_QUEUE_NAME = "payment.failed.queue";
  public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
  public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

  @Bean
  public DirectExchange exchange() {
    return new DirectExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue inventoryQueue() {
    return new Queue(INVENTORY_QUEUE_NAME);
  }

  @Bean
  public Binding binding(Queue inventoryQueue, DirectExchange exchange) {
    return BindingBuilder.bind(inventoryQueue).to(exchange).with(INVENTORY_ROUTING_KEY);
  }

  @Bean
  public Queue inventoryReleaseQueue() {
    return new Queue(INVENTORY_QUEUE_NAME);
  }

  @Bean
  public Binding iventoryReleaseBinding(Queue inventoryReleaseQueue, DirectExchange exchange) {
    return BindingBuilder.bind(inventoryReleaseQueue).to(exchange).with(INVENTORY_ROUTING_KEY);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public Queue orderReservedQueue() {
    return new Queue(ORDER_RESERVED_QUEUE_NAME);
  }

  @Bean
  public Binding reservedBinding(Queue orderReservedQueue, DirectExchange exchange) {
    return BindingBuilder.bind(orderReservedQueue).to(exchange).with(ORDER_RESERVED_ROUTING_KEY);
  }

  @Bean
  public Queue orderRejectedQueue() {
    return new Queue(ORDER_REJECTED_QUEUE_NAME);
  }

  @Bean
  public Binding rejectedBinding(Queue orderRejectedQueue, DirectExchange exchange) {
    return BindingBuilder.bind(orderRejectedQueue).to(exchange).with(ORDER_REJECTED_ROUTING_KEY);
  }

  @Bean
  public Queue paymentSuccessQueue() {
    return new Queue(PAYMENT_SUCCESS_QUEUE_NAME);
  }

  @Bean
  public Binding paymentSuccessBinding(Queue paymentSuccessQueue, DirectExchange exchange) {
    return BindingBuilder.bind(paymentSuccessQueue).to(exchange).with(PAYMENT_SUCCESS_ROUTING_KEY);
  }

  @Bean
  public Queue paymentFailedQueue() {
    return new Queue(PAYMENT_FAILED_QUEUE_NAME);
  }

  @Bean
  public Binding paymentFailedBinding(Queue paymentFailedQueue, DirectExchange exchange) {
    return BindingBuilder.bind(paymentFailedQueue).to(exchange).with(PAYMENT_FAILED_ROUTING_KEY);
  }
}
