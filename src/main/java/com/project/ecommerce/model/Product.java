package com.project.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data // Esto genera getters, setters y toString automáticamente
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private int stock;

    private boolean active = true;

    @Version // Esto evita la sobreventa (concurrencia optimista)
    private Long version;
    
    // Añade esto dentro de la clase Product
    private boolean deleted = false; // Para el Soft Delete

    @Column(updatable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

}