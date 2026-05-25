package com.project.ecommerce.event;

import java.io.Serializable;

public record PaymentFailedEvent(
    Long orderId,
    String reason
) implements Serializable {}