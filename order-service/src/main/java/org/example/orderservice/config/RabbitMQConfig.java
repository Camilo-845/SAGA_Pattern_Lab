package org.example.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public class RabbitMQConfig {
  public static final String EXCHANGE_NAME = "saga.exchange";
  public static final String INVENTORY_QUEUE_NAME = "inventory.queue";
  public static final String INVENTORY_ROUTING_KEY = "inventory.reserve";

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
}
