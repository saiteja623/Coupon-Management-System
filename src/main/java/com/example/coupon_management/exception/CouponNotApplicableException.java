package com.example.coupon_management.exception;

public class CouponNotApplicableException extends  RuntimeException{

    public CouponNotApplicableException(String message){
        super(message);
    }
}
