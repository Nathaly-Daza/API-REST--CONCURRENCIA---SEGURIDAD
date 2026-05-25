package com.project.ecommerce.event;

import java.io.Serializable;

public record OrderItemEvent(
    Long productId,
    int quantity,
    double price
) implements Serializable {}