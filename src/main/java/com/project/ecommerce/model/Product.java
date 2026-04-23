package com.project.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data 
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
    
    
    private boolean deleted = false; // Para el Soft Delete

    @Column(updatable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

}