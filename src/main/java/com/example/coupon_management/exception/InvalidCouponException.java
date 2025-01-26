package com.example.coupon_management.exception;

public class InvalidCouponException extends RuntimeException{

    public InvalidCouponException(String message){
        super(message);
    }
}
