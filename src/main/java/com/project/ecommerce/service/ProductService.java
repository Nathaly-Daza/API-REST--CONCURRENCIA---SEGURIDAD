package com.project.ecommerce.service;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.dto.ProductRequest;
import com.project.ecommerce.dto.ProductResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        
        Product saved = productRepository.save(product);
        return new ProductResponse(saved.getId(), saved.getName(), saved.getPrice(), saved.getStock());
    }

    @Transactional
    public void buyProduct(Long productId, int quantity) {
        // Buscamos el producto
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Validamos stock
        if (product.getStock() < quantity) {
            throw new RuntimeException("No hay stock suficiente para " + product.getName());
        }

        // Restamos stock
        product.setStock(product.getStock() - quantity);

        // Guardamos. Gracias al @Version en el Model, si alguien más
        // compró el mismo producto al mismo tiempo, esto lanzará una excepción
        // evitando la sobreventa.
        productRepository.save(product);
    }
    
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                .toList();
    }
    
    public void deleteProduct(Long id) {
        Product p = productRepository.findById(id).orElseThrow();
        p.setDeleted(true); // No lo borramos de la DB, solo lo marcamos
        productRepository.save(p);
    }

    public ProductResponse update(Long id, ProductRequest req) {
        Product p = productRepository.findById(id).orElseThrow();

        p.setName(req.name());
        p.setPrice(req.price());
        p.setStock(req.stock());

        productRepository.save(p);

        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getStock());
    }

}
