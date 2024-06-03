package com.ecommerceapp.PaymentService.service;

import com.ecommerceapp.PaymentService.model.PaymentRequest;
import com.ecommerceapp.PaymentService.model.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(Long orderId);
}
