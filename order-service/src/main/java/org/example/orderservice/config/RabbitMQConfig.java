package org.example.orderservice.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  public static final String EXCHANGE_NAME = "saga.exchange";
  public static final String INVENTORY_QUEUE_NAME = "inventory.queue";
  public static final String INVENTORY_ROUTING_KEY = "inventory.reserve";

  public static final String ORDER_RESERVED_QUEUE_NAME = "order.reserved.queue";
  public static final String ORDER_REJECTED_QUEUE_NAME = "order.rejected.queue";
  public static final String ORDER_RESERVED_ROUTING_KEY = "order.reserved";
  public static final String ORDER_REJECTED_ROUTING_KEY = "order.rejected";

  public static final String PAYMENT_ROUTING_KEY = "payment.process";

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
}
