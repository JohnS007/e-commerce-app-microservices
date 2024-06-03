package com.ecommerceapp.OrderService.service;


import com.ecommerceapp.OrderService.entity.Order;
import com.ecommerceapp.OrderService.exception.CustomException;
import com.ecommerceapp.OrderService.external.client.PaymentService;
import com.ecommerceapp.OrderService.external.client.ProductService;
import com.ecommerceapp.OrderService.external.request.PaymentRequest;
import com.ecommerceapp.OrderService.external.response.PaymentResponse;
import com.ecommerceapp.OrderService.model.OrderRequest;
import com.ecommerceapp.OrderService.model.OrderResponse;
import com.ecommerceapp.OrderService.model.ProductResponse;
import com.ecommerceapp.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {

        //Order Entity -> Save the data with Status Order Created
        //Product Service - Block Products (Reduce the Quantity)
        //Payment Service -> Payments -> Success-> COMPLETE, Else
        //CANCELLED

        log.info("Placing order request: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating order with Status as CREATED");

        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling payment service to complete the payment");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;

        try{
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully. Chaging the order status to PLACED");
            orderStatus = "PLACED";
        }
        catch (Exception exception){
            log.error("Error occured in payment. Changing the order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order placed successfully for order id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId) {
        log.info("Get order details for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new CustomException("Order not found for the orderId: " + orderId,
                        "NOT_FOUND",
                        404));

        log.info("Invoking product service to fetch the product details for productId: {} ", order.getProductId());

        ProductResponse productResponse = restTemplate.getForObject("http://ProductService/product/" + order.getProductId(),
                ProductResponse.class
                );

        log.info("Getting payment information from payment service ");

        PaymentResponse paymentResponse = restTemplate.getForObject("http://PaymentService/payment/order/" + order.getId(),
                PaymentResponse.class);

        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentDate(paymentResponse.getPaymentDate())
                .amount(paymentResponse.getAmount())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();

        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .quantity(productResponse.getQuantity())
                .price(productResponse.getPrice())
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();


        return orderResponse;
    }
}
