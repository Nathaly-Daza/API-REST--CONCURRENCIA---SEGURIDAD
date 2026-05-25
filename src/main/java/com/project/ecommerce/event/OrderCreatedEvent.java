package com.project.ecommerce.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreatedEvent(
    Long orderId,
    String username,
    double total,
    List<OrderItemEvent> items,
    LocalDateTime timestamp
) implements Serializable {}