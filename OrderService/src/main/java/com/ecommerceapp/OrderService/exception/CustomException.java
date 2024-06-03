package com.ecommerceapp.OrderService.exception;

import lombok.Data;

@Data
public class CustomException extends RuntimeException{

    private String errorCode;
    private int status;

    public CustomException(String errorMessage, String errorCode, int status){
        super(errorMessage);
        this.errorCode = errorCode;
        this.status = status;
    }

}
