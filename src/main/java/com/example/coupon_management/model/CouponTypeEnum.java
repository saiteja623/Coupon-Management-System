package com.example.coupon_management.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CouponTypeEnum {

    CART("cart-wise"),
    PRODUCT("product-wise"),
    BxGy("bxgy");

    private final String value;
    CouponTypeEnum(String value) {
        this.value =value;
    }

    @JsonValue
    public  String getValue() {
        return value;
    }

    public static CouponTypeEnum getCouponByType(String value){
        for(CouponTypeEnum coupon: CouponTypeEnum.values()){
            if(coupon.getValue().equalsIgnoreCase(value))
                return coupon;
        }
        return null;
    }
    public static boolean isValidCoupon(String couponType){
        for(CouponTypeEnum coupon : CouponTypeEnum.values()){
            if(coupon.getValue().equalsIgnoreCase(couponType)){
                return true;
            }
        }
        return false;
    }

}
