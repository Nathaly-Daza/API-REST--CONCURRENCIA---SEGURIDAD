// com.project.ecommerce.model.OrderStatus.java
package com.project.ecommerce.model;

public enum OrderStatus {
    PENDING,      // Orden creada, esperando pago
    CONFIRMED,    // Pago exitoso
    CANCELLED,    // Pago fallido o cancelada
    COMPENSATED   // Stock liberado después de cancelación
}