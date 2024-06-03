package com.ecommerceapp.OrderService.external.request;


import com.ecommerceapp.OrderService.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private Long orderId;
    private Long amount;
    private String referenceNumber;
    private PaymentMode paymentMode;
}
