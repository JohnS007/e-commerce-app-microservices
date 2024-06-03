package com.ecommerceapp.OrderService.controller;

import com.ecommerceapp.OrderService.model.OrderRequest;
import com.ecommerceapp.OrderService.model.OrderResponse;
import com.ecommerceapp.OrderService.service.OrderService;
import jakarta.ws.rs.Path;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('Customer')")
    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest){
        Long orderId = orderService.placeOrder(orderRequest);
        log.info("Order id: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable("orderId") Long orderId){
        OrderResponse orderResponse = orderService.getOrderDetails(orderId);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }
}
