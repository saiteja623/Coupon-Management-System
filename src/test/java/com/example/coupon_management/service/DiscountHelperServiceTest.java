package com.example.coupon_management.service;

import com.example.coupon_management.dto.CartDTO;
import com.example.coupon_management.dto.CouponDTO;
import com.example.coupon_management.entity.CouponEntity;
import com.example.coupon_management.model.CartItem;
import com.example.coupon_management.model.CouponDetails;
import com.example.coupon_management.model.CouponTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DiscountHelperServiceTest {

    @InjectMocks
    private DiscountHelperService discountHelperService;

    private static CouponDTO cartWiseCoupon;
    private static CouponDTO productWiseCoupon;
    private static CartDTO cart;

    @BeforeAll
    public static void setUpData(){
        cartWiseCoupon = createCartWiseCoupon();
        productWiseCoupon = createProductWiseCoupon();
        cart = createCart();
    }


    @Test
    public void getCartWiseCouponDiscountTest() {
        int discount  =  discountHelperService.getCouponDiscount(cartWiseCoupon,cart);
        Assertions.assertEquals(44,discount);
    }

    @Test
    public void getProductWiseCouponDiscountTest(){
        int discount  = discountHelperService.getCouponDiscount(productWiseCoupon,cart);
        Assertions.assertEquals(88,discount);
    }


    private static CouponDTO createCartWiseCoupon() {
        CouponDetails couponDetails = CouponDetails.builder()
                .discount(10)
                .threshold(100)
                .build();
        return CouponDTO.builder()
                .type(CouponTypeEnum.CART.getValue())
                .details(couponDetails)
                .build();
    }

    private static CartDTO createCart(){
        return CartDTO.builder().items(List.of(CartItem.builder().productId(1).quantity(1).price(440).build())).build();
    }

    private static CouponDTO createProductWiseCoupon(){
        CouponDetails couponDetails = CouponDetails.builder()
                .discount(20)
                .productId(1)
                .build();
        return CouponDTO.builder()
                .type(CouponTypeEnum.PRODUCT.getValue())
                .details(couponDetails)
                .build();
    }

}
