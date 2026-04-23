package com.project.ecommerce.controller;

import com.project.ecommerce.dto.OrderItemRequest;
import com.project.ecommerce.dto.OrderRequest;
import com.project.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/concurrency")
    public String testConcurrency(@RequestParam Long productId, @RequestParam String username) {

        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    OrderRequest request = new OrderRequest(
                        List.of(new OrderItemRequest(productId, 1))
                    );

                    orderService.createOrder(username, request);

                    System.out.println("Compra exitosa");
                } catch (Exception e) {
                    System.err.println("Error esperado: " + e.getMessage());
                }
            });
        }

        return "Prueba enviada. Revisa la consola.";
    }
}