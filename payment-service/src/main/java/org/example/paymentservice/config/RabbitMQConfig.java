package org.example.paymentservice.config;

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
  public static final String PAYMENT_QUEUE_NAME = "payment.queue";
  public static final String PAYMENT_ROUTING_KEY = "payment.process";

  public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
  public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

  @Bean
  public DirectExchange exchange() {
    return new DirectExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue paymentQueue() {
    return new Queue(PAYMENT_QUEUE_NAME);
  }

  @Bean
  public Binding binding(Queue paymentQueue, DirectExchange exchange) {
    return BindingBuilder.bind(paymentQueue).to(exchange).with(PAYMENT_ROUTING_KEY);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
