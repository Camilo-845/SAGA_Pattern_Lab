package org.example.orderservice.dto.event;

public record OrderCompletedEvent(
    String orderId) {
}
