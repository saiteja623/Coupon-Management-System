package com.example.coupon_management.service;

import com.example.coupon_management.dto.CartDTO;
import com.example.coupon_management.dto.CouponDTO;
import com.example.coupon_management.model.CouponTypeEnum;
import org.springframework.stereotype.Service;

@Service
public class DiscountHelperService {


    public int getCouponDiscount(CouponDTO coupon, CartDTO cart){
        int discount = 0;
        CouponTypeEnum couponTypeEnum =  CouponTypeEnum.getCouponByType(coupon.getType());
        discount = switch (couponTypeEnum) {
            case CART -> getCartWiseDiscount(coupon, cart);
            case PRODUCT -> getProductWiseDiscount(coupon, cart);
            case BxGy -> getBxGyDiscount(coupon, cart);
        };
        return discount;
    }

    private int getCartWiseDiscount(CouponDTO coupon,CartDTO cart){
        int totalPrice = cart.getItems().stream()
                .mapToInt(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                .sum();
        if(totalPrice >= coupon.getDetails().getThreshold()) {
            return calculateDiscount(totalPrice, coupon.getDetails().getDiscount());
        }
        return 0;
    }

    private int getProductWiseDiscount(CouponDTO couponDTO,CartDTO cart){
        return cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(couponDTO.getDetails().getProductId()))
                .findFirst()
                .map(cartItem ->  calculateDiscount(cartItem.getQuantity() * cartItem.getPrice(), couponDTO.getDetails().getDiscount()))
                .orElse(0);
    }


    private int getBxGyDiscount(CouponDTO couponDTO,CartDTO cart){
        //add bxgy logic to check if coupon is applicable
        return 0;
    }

    public  int calculateDiscount(int price, int discPercentage){
        return (price * discPercentage) / 100;
    }
}
