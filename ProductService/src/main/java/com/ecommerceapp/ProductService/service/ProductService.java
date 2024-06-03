package com.ecommerceapp.ProductService.service;

import com.ecommerceapp.ProductService.model.ProductRequest;
import com.ecommerceapp.ProductService.model.ProductResponse;

public interface ProductService {
    Long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
