package com.example.coupon_management.exception;


public class CouponNotFoundException extends RuntimeException{

    public CouponNotFoundException(String message){
        super(message);
    }
}
