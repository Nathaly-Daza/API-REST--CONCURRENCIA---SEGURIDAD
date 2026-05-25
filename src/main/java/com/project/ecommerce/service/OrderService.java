package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderRequest;
import com.project.ecommerce.event.OrderCreatedEvent;
import com.project.ecommerce.event.OrderItemEvent;
import com.project.ecommerce.model.*;
import com.project.ecommerce.repository.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
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
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Value("${app.rabbitmq.exchange}")
    private String exchange;
    
    @Value("${app.rabbitmq.routing-key.order-created}")
    private String orderCreatedRoutingKey;

    @Transactional
    public Order createOrder(String username, OrderRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        double total = 0;
        List<OrderItemEvent> itemEvents = new ArrayList<>();

        for (var itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (product.getStock() < itemRequest.quantity()) {
                throw new RuntimeException("Stock insuficiente para " + product.getName());
            }

            // Descontar stock con control de concurrencia por @Version
            product.setStock(product.getStock() - itemRequest.quantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setPrice(product.getPrice());
            item.setOrder(order);
            
            total += item.getPrice() * item.getQuantity();
            order.getItems().add(item);
            
            itemEvents.add(new OrderItemEvent(product.getId(), itemRequest.quantity(), product.getPrice()));
        }

        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);

        // Publicar evento al broker
        OrderCreatedEvent event = new OrderCreatedEvent(
            savedOrder.getId(), username, total, itemEvents, java.time.LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend(exchange, orderCreatedRoutingKey, event);
        System.out.println("✅ Orden #" + savedOrder.getId() + " creada en estado PENDING. Evento enviado a RabbitMQ");

        // Auditoría
        AuditLog log = new AuditLog();
        log.setAction("ORDER_CREATED");
        log.setUsername(username);
        log.setDetails("Orden #" + savedOrder.getId() + " creada por $" + total);
        auditLogRepository.save(log);

        return savedOrder;
    }

    @Transactional
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        
        AuditLog log = new AuditLog();
        log.setAction("ORDER_CONFIRMED");
        log.setUsername(order.getUser().getUsername());
        log.setDetails("Orden #" + orderId + " confirmada");
        auditLogRepository.save(log);
        
        System.out.println(" Orden #" + orderId + " CONFIRMADA");
    }

    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        if (order.getStatus() != OrderStatus.PENDING) {
            System.out.println(" La orden #" + orderId + " ya fue procesada");
            return;
        }
        
        // Liberar stock (compensación)
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
            System.out.println(" Stock liberado: " + product.getName() + " +" + item.getQuantity());
        }
        
        order.setStatus(OrderStatus.COMPENSATED);
        orderRepository.save(order);
        
        AuditLog log = new AuditLog();
        log.setAction("ORDER_COMPENSATED");
        log.setUsername(order.getUser().getUsername());
        log.setDetails("Orden #" + orderId + " compensada. Razón: " + reason);
        auditLogRepository.save(log);
        
        System.out.println(" Orden #" + orderId + " COMPENSADA. Razón: " + reason);
    }
}