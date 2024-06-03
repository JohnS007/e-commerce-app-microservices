package com.ecommerceapp.PaymentService.service;

import com.ecommerceapp.PaymentService.entity.TransactionDetails;
import com.ecommerceapp.PaymentService.model.PaymentMode;
import com.ecommerceapp.PaymentService.model.PaymentRequest;
import com.ecommerceapp.PaymentService.model.PaymentResponse;
import com.ecommerceapp.PaymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details: {}", paymentRequest);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .amount(paymentRequest.getAmount())
                .build();

        transactionDetailsRepository.save(transactionDetails);

        log.info("Transaction completed with transaction id: {}", transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(Long orderId) {
        log.info("Getting payment details for the orderId: {}", orderId);

        TransactionDetails transactionDetails = transactionDetailsRepository.findByOrderId(orderId);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .orderId(transactionDetails.getOrderId())
                .status(transactionDetails.getPaymentStatus())
                .amount(transactionDetails.getAmount())
                .build();

        return paymentResponse;

    }
}
