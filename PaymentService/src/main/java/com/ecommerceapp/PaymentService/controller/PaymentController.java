package com.ecommerceapp.PaymentService.controller;

import com.ecommerceapp.PaymentService.model.PaymentRequest;
import com.ecommerceapp.PaymentService.model.PaymentResponse;
import com.ecommerceapp.PaymentService.service.PaymentService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
        return new ResponseEntity<>(paymentService.doPayment(paymentRequest), HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable("orderId") Long orderId){
        return new ResponseEntity<>(paymentService.getPaymentDetailsByOrderId(orderId),
                HttpStatus.OK);
    }
}
