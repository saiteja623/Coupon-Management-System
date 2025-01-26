package com.example.coupon_management.service;

import com.example.coupon_management.dto.CartDTO;
import com.example.coupon_management.dto.CouponDTO;
import com.example.coupon_management.exception.CouponNotApplicableException;
import com.example.coupon_management.model.CartItem;
import com.example.coupon_management.model.CouponTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponManagerService {

    @Autowired
    private DiscountHelperService discountHelperService;
    public CartDTO applyCoupon(CouponDTO coupon, CartDTO cart){
        return switch (CouponTypeEnum.getCouponByType(coupon.getType())){
            case CART -> applyCartWiseCoupon(coupon,cart);
            case PRODUCT -> applyProductWiseCoupon(coupon,cart);
            case BxGy -> applyBxGyCoupon(coupon,cart);
        };
    }

    private CartDTO applyCartWiseCoupon(CouponDTO coupon, CartDTO cart){
        int totalPrice = calculateTotalPrice(cart);
        int discount = 0;
        int finalprice = totalPrice;
        if(totalPrice >= coupon.getDetails().getThreshold()){
            discount = discountHelperService.calculateDiscount(totalPrice, coupon.getDetails().getDiscount());
            finalprice = totalPrice - discount;
        }
        else{
            throw new CouponNotApplicableException("Cart total price should be greater than " + coupon.getDetails().getThreshold() + " for the coupon to be applicable.");
        }
        return CartDTO.builder()
                .items(cart.getItems())
                .totalPrice(totalPrice)
                .totalDiscount(discount)
                .finalPrice(finalprice)
                .build();
    }

    private CartDTO applyProductWiseCoupon(CouponDTO coupon, CartDTO cart) {
        int totalPrice = calculateTotalPrice(cart);
        int discount = 0;
        int finalPrice = totalPrice;
        CartDTO updatedCart = CartDTO.builder().items(cart.getItems()).totalPrice(totalPrice).build();

        boolean isProductFound = false;
        for(CartItem  cartItem : updatedCart.getItems()){
            if(cartItem.getProductId().equals(coupon.getDetails().getProductId())){
                discount = discountHelperService.calculateDiscount(cartItem.getQuantity() * cartItem.getPrice() ,coupon.getDetails().getDiscount());
                cartItem.setTotalDiscount(discount);
                finalPrice = totalPrice - discount;
                isProductFound = true;
                break;
            }
        }
        if(!isProductFound){
            throw new CouponNotApplicableException("No product found for the product wise coupon id " + coupon.getCouponId());
        }
        updatedCart.setTotalDiscount(discount);
        updatedCart.setFinalPrice(finalPrice);
        return updatedCart;

    }

    private CartDTO applyBxGyCoupon(CouponDTO coupon, CartDTO cart) {
        //BxGy logic goes here
        return cart;
    }

    private int calculateTotalPrice(CartDTO cart){
        return cart.getItems().stream()
                .mapToInt(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                .sum();
    }


}
