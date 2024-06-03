package com.ecommerceapp.ProductService.service;

import com.ecommerceapp.ProductService.entity.Product;
import com.ecommerceapp.ProductService.exception.ProductServiceCustomException;
import com.ecommerceapp.ProductService.model.ProductRequest;
import com.ecommerceapp.ProductService.model.ProductResponse;
import com.ecommerceapp.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Log4j2
public class ProductServiceIImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Long addProduct(ProductRequest productRequest) {
        log.info("Adding product...");
        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product created..!!");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for product id: " + productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductServiceCustomException("Product with given id is not found",
                                "PRODUCT_NOT_FOUND"));

        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);

        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {

        log.info("Reduce quantity: {}, for productId: {}", quantity, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductServiceCustomException("Product with given id not found", "PRODUCT_NOT_FOUND"));
        if(product.getQuantity() < quantity){
            throw new ProductServiceCustomException("Product with given quantity is not available",
                    "INSUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);

        log.info("Product quantity updated successfully");
    }
}
