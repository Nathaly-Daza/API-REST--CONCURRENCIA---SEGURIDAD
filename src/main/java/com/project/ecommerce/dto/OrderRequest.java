package com.project.ecommerce.dto;


import java.util.List;

public record OrderRequest(List<OrderItemRequest> items) {}