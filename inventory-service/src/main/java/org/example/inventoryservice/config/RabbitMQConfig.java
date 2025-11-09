package org.example.inventoryservice.config;

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

  public static final String ORDER_RESERVED_ROUTING_KEY = "order.reserved";
  public static final String ORDER_REJECTED_ROUTING_KEY = "order.rejected";

  public static final String INVENTORY_RELEASE_QUEUE_NAME = "inventory.release.queue";
  public static final String INVENTORY_RELEASE_ROUTING_KEY = "inventory.release";

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
  public Queue inventoryReleaseQueue() {
    return new Queue(INVENTORY_RELEASE_QUEUE_NAME);
  }

  @Bean
  public Binding inventoryReleaseBinding(Queue inventoryReleaseQueue, DirectExchange exchange) {
    return BindingBuilder.bind(inventoryReleaseQueue).to(exchange).with(INVENTORY_RELEASE_ROUTING_KEY);
  }
}
