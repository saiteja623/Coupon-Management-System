package com.example.coupon_management.service;

import com.example.coupon_management.dto.CartDTO;
import com.example.coupon_management.dto.CouponDTO;
import com.example.coupon_management.exception.CouponNotApplicableException;
import com.example.coupon_management.model.CartItem;
import com.example.coupon_management.model.CouponDetails;
import com.example.coupon_management.model.CouponTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class CouponManagerServiceTest {

    @InjectMocks
    private CouponManagerService couponManagerService;
    @Mock
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
    public void applyCartWiseCouponTest(){
        Mockito.when(discountHelperService.calculateDiscount(anyInt(),anyInt())).thenReturn(44);
        CartDTO updatedCart = couponManagerService.applyCoupon(cartWiseCoupon,cart);
        Assertions.assertNotNull(updatedCart);
        Assertions.assertEquals(44, updatedCart.getTotalDiscount());
        Assertions.assertEquals(396, updatedCart.getFinalPrice());
    }

    @Test
    public void cartWiseCouponNotApplicableTest(){
        cart.getItems().get(0).setPrice(90);
        Assertions.assertThrows(CouponNotApplicableException.class ,() -> couponManagerService.applyCoupon(cartWiseCoupon,cart));
    }

    @Test
    public void applyProductWiseCoupon(){
        Mockito.when(discountHelperService.calculateDiscount(anyInt(),anyInt())).thenReturn(88);
        CartDTO updatedCart = couponManagerService.applyCoupon(productWiseCoupon,cart);
        Assertions.assertNotNull(updatedCart);
        Assertions.assertEquals(88, updatedCart.getTotalDiscount());
        Assertions.assertEquals(352, updatedCart.getFinalPrice());
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
