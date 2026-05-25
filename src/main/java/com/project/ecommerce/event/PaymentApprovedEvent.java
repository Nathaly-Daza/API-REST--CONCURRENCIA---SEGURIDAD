package com.project.ecommerce.event;

import java.io.Serializable;

public record PaymentApprovedEvent(
    Long orderId,
    String message
) implements Serializable {
    public PaymentApprovedEvent(Long orderId) {
        this(orderId, "Pago aprobado exitosamente");
    }
}