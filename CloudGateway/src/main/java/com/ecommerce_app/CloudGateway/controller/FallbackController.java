package com.ecommerce_app.CloudGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallback(){
        return "Order Service is Down !";
    }

    @GetMapping("/productServiceFallBack")
    public String productServiceFallBack(){
        return "Product Service is Down !";
    }

    @GetMapping("/paymentServiceFallBack")
    public String paymentServiceFallBack(){
        return "Payment Service is Down !";
    }
}
