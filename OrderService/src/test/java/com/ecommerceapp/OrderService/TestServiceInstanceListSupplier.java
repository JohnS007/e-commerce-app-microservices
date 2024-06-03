package com.ecommerceapp.OrderService;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {
    @Override
    public String getServiceId() {
        return "";
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance> serviceInstanceList = new ArrayList<>();

        serviceInstanceList.add(new DefaultServiceInstance(
                "PaymentService",
                "PaymentService",
                "localhost",
                8080,
                false
        ));

        serviceInstanceList.add(new DefaultServiceInstance(
                "ProductService",
                "ProductService",
                "localhost",
                8080,
                false
        ));

        return Flux.just(serviceInstanceList);
    }
}
