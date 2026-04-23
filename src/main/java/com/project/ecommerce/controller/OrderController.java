package com.project.ecommerce.controller;


import com.project.ecommerce.dto.OrderRequest;
import com.project.ecommerce.model.Order;
import com.project.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Order create(@RequestBody OrderRequest request,
                        @RequestHeader("Authorization") String token) {

        String username = token.substring(7); // simple
        return service.createOrder(username, request);
    }
}