package com.ecommerceapp.OrderService.service;

import com.ecommerceapp.OrderService.model.OrderRequest;
import com.ecommerceapp.OrderService.model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(Long orderId);
}
