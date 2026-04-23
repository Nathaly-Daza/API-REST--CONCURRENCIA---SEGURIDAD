package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderRequest;
import com.project.ecommerce.model.*;
import com.project.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public Order createOrder(String username, OrderRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        Order order = new Order();
        order.setUser(user);

        double total = 0;

        List<OrderItem> items = request.items().stream().map(i -> {

            Product product = productRepository.findById(i.productId())
                    .orElseThrow();

            if (product.getStock() < i.quantity()) {
                throw new RuntimeException("Stock insuficiente");
            }

            product.setStock(product.getStock() - i.quantity());

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(i.quantity());
            item.setPrice(product.getPrice());

            return item;

        }).toList();

        for (OrderItem i : items) {
            i.setOrder(order);
            total += i.getPrice() * i.getQuantity();
        }

        order.setItems(items);
        order.setTotal(total);

        return orderRepository.save(order);
    }
}
