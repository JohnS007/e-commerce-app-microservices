package com.ecommerceapp.OrderService.service;


import com.ecommerceapp.OrderService.entity.Order;
import com.ecommerceapp.OrderService.exception.CustomException;
import com.ecommerceapp.OrderService.external.client.PaymentService;
import com.ecommerceapp.OrderService.external.client.ProductService;
import com.ecommerceapp.OrderService.external.request.PaymentRequest;
import com.ecommerceapp.OrderService.external.response.PaymentResponse;
import com.ecommerceapp.OrderService.model.OrderRequest;
import com.ecommerceapp.OrderService.model.OrderResponse;
import com.ecommerceapp.OrderService.model.PaymentMode;
import com.ecommerceapp.OrderService.model.ProductResponse;
import com.ecommerceapp.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();


    @Test
    @DisplayName("GetOrderDetails - Success Scenario")
    public void test_when_order_success(){
        
        Order order = getMockOrder();
        
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        
        when(restTemplate.getForObject("http://ProductService/product/" + order.getProductId(),
                ProductResponse.class
        )).thenReturn(getMockProductResponse());
        
        when( restTemplate.getForObject("http://PaymentService/payment/order/" + order.getId(),
                PaymentResponse.class)).thenReturn(getMockPaymentResponse());
        
        OrderResponse orderResponse = orderService.getOrderDetails(1L);

        verify(orderRepository, times(1)).findById(anyLong());

        verify(restTemplate, times(1)).getForObject("http://ProductService/product/" + order.getProductId(),
                ProductResponse.class
        );

        verify(restTemplate, times(1)).getForObject("http://PaymentService/payment/order/" + order.getId(),
                PaymentResponse.class);

        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());

    }

    @DisplayName("Get Orders - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found() {

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception =
                assertThrows(CustomException.class,
                        () -> orderService.getOrderDetails(1L));
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());

        verify(orderRepository, times(1))
                .findById(anyLong());
    }


    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productService, times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }


    @DisplayName("Place Order - Payment Failed Scenario")
    @Test
    void test_when_Place_Order_Payment_Fails_then_Order_Placed() {

        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productService, times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }



    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1L)
                .quantity(10L)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100L)
                .build();
    }


    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1L)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200L)
                .orderId(1L)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2L)
                .productName("iPhone")
                .price(100L)
                .quantity(200L)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1L)
                .amount(100L)
                .quantity(200L)
                .productId(2L)
                .build();
    }
}