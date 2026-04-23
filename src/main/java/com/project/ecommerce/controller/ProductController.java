package com.project.ecommerce.controller;

import com.project.ecommerce.dto.ProductRequest;
import com.project.ecommerce.dto.ProductResponse;
import com.project.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    // Endpoint para crear productos
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    // Endpoint para comprar (simular concurrencia)
    @PostMapping("/{id}/buy")
    public ResponseEntity<String> buy(@PathVariable Long id, @RequestParam int quantity) {
        service.buyProduct(id, quantity);
        return ResponseEntity.ok("Compra realizada con éxito");
    }
    
    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(service.findAll()); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.ok("Producto eliminado");
    }
}
