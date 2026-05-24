package com.project.ecommerce.controller;

import com.project.ecommerce.dto.OrderRequest;
import com.project.ecommerce.model.Order;
import com.project.ecommerce.security.JwtUtil;
import com.project.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public Order create(@RequestBody OrderRequest request,
                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Quita "Bearer "
        String username = jwtUtil.extractUsername(token); // Extrae username REAL
        return service.createOrder(username, request);
    }
}