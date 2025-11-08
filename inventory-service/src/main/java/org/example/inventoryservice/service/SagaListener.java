package org.example.inventoryservice.service;

import java.math.BigDecimal;

import org.example.inventoryservice.config.RabbitMQConfig;
import org.example.inventoryservice.dto.command.ReserveInvetoryCommand;
import org.example.inventoryservice.dto.event.InventoryRejectedEvent;
import org.example.inventoryservice.dto.event.InventoryReserveEvent;
import org.example.inventoryservice.model.Inventory;
import org.example.inventoryservice.repository.InventoryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SagaListener {
  @Autowired
  private InventoryRepository inventoryRepository;
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Transactional
  @RabbitListener(queues = RabbitMQConfig.INVENTORY_QUEUE_NAME)
  public void handleReserveInventory(ReserveInvetoryCommand command) {
    System.out.println("ReserveInventoryCommand recibido para la orden: " + command.orderId());

    Inventory item = inventoryRepository.findById(command.productId());

    if (item != null && item.getAvaliableQuantity() >= command.quantity()) {
      item.setAvaliableQuantity(item.getAvaliableQuantity() - command.quantity());
      inventoryRepository.save(item);

      BigDecimal totalAmount = item.getPrice().multiply(new BigDecimal(command.quantity()));

      InventoryReserveEvent event = new InventoryReserveEvent(
          command.orderId(),
          command.productId(),
          command.quantity(),
          totalAmount);

      rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ORDER_RESERVED_ROUTING_KEY, event);
      System.out.println("Inventario reservado. Enviando InventoryReservedEvent para la orden: " + command.orderId());
    } else {
      String reason = (item == null) ? "Producto no encontrado" : "Stock Insuficiente";

      InventoryRejectedEvent event = new InventoryRejectedEvent(
          command.orderId(),
          command.productId(),
          command.quantity(),
          reason);

      rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ORDER_REJECTED_ROUTING_KEY, event);
      System.out.println("Stock insuficiente. Enviando InventoryRejectedEven para la orden: " + command.orderId());
    }
  }
}
