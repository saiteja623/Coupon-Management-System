package com.example.coupon_management.service;

import com.example.coupon_management.dto.CartDTO;
import com.example.coupon_management.dto.CouponDTO;
import com.example.coupon_management.entity.CouponEntity;
import com.example.coupon_management.exception.CouponNotFoundException;
import com.example.coupon_management.exception.InvalidCouponException;
import com.example.coupon_management.model.CartItem;
import com.example.coupon_management.model.CouponDetails;
import com.example.coupon_management.model.CouponTypeEnum;
import com.example.coupon_management.repository.CouponRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private DiscountHelperService discountHelperService;
    @Mock
    private CouponManagerService couponManagerService;
    @Mock
    private ObjectMapper objectMapper;

    private static CouponEntity couponEntity;
    private static CouponDTO cartWiseCoupon;
    private static CartDTO cart;

    @BeforeAll
    public static void setUpData(){
         couponEntity = createCouponEntity();
        cartWiseCoupon = createCartWiseCoupon();
        cart = createCart();
    }

    @Test
    public void createCouponTest() {
        CouponDTO cartCoupon = cartWiseCoupon;
        Mockito.when(couponRepository.save(any())).thenReturn(couponEntity);
        CouponDTO savedCoupon = couponService.createCoupon(cartCoupon);
        Assertions.assertNotNull(savedCoupon);
        Assertions.assertEquals(1L, savedCoupon.getCouponId());
    }

    @Test
    public void createCouponExceptionTest() {
        CouponDTO couponDTO = CouponDTO.builder().type("invalid").build();
        Assertions.assertThrows(InvalidCouponException.class, () -> couponService.createCoupon(couponDTO));
    }

    @Test
    public void getAllCouponsTest() {
        Mockito.when(couponRepository.findAll()).thenReturn(List.of(couponEntity));
        List<CouponDTO> coupons = couponService.getAllCoupons();
        Assertions.assertNotNull(coupons);
        Assertions.assertNotEquals(0, coupons.size());
        Assertions.assertEquals(1L, coupons.get(0).getCouponId());
    }

    @Test
    public void getCouponById_validTest() {
        Mockito.when(couponRepository.findById(any())).thenReturn(Optional.ofNullable(couponEntity));
        CouponDTO couponDTO = couponService.getCouponById(1L);
        Assertions.assertNotNull(couponDTO);
        Assertions.assertEquals(1L, couponDTO.getCouponId());
    }

    @Test
    public void getCouponById_inValidTest() {
        Mockito.when(couponRepository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(CouponNotFoundException.class, () -> couponService.getCouponById(2L));
    }

    @Test
    public void updateCouponById_validTest(){
        Mockito.when(couponRepository.findById(any())).thenReturn(Optional.ofNullable(couponEntity));
        Mockito.when(couponRepository.save(any())).thenReturn(couponEntity);
        cartWiseCoupon.setType(CouponTypeEnum.CART.getValue());
        CouponDTO updatedCoupon = couponService.updateCouponById(1L,cartWiseCoupon);
        Assertions.assertNotNull(updatedCoupon);
        Assertions.assertEquals(1L, updatedCoupon.getCouponId());
    }

    @Test
    public void updateCouponById_inValidTest(){
        CouponDTO couponDTO = cartWiseCoupon;
        couponDTO.setType("invalid type");
        Assertions.assertThrows(InvalidCouponException.class , () -> couponService.updateCouponById(1L,couponDTO));
    }

    @Test
    public void getApplicableCouponsTest(){
        Mockito.when(couponRepository.findAll()).thenReturn(List.of(couponEntity));
        Mockito.when(discountHelperService.getCouponDiscount(any(),any())).thenReturn(44);
        List<CouponDTO> applicableCoupons = couponService.getApplicableCoupons(cart);
        Assertions.assertNotEquals(0, applicableCoupons.size());
        Assertions.assertEquals(44, applicableCoupons.get(0).getDiscount());
    }

    @Test
    public void applyCouponTest(){
        CartDTO cartAfterDiscount = cart;
        cartAfterDiscount.setTotalDiscount(44);
        cartAfterDiscount.setTotalPrice(440);
        Mockito.when(couponRepository.findById(any())).thenReturn(Optional.of(couponEntity));
        Mockito.when(couponManagerService.applyCoupon(any(),any())).thenReturn(cartAfterDiscount);
        CartDTO cartActual = couponService.applyCoupon(1L,cart);
        Assertions.assertNotNull(cartActual);
        Assertions.assertEquals(44,cartActual.getTotalDiscount());
        Assertions.assertEquals(440,cartActual.getTotalPrice());
    }

    @Test
    public void applyCouponExceptionTest(){
        CouponEntity expiredCoupon = couponEntity;
        expiredCoupon.setExpireDate(LocalDate.now().minusDays(2));
        Mockito.when(couponRepository.findById(any())).thenReturn(Optional.of(expiredCoupon));
        Assertions.assertThrows(InvalidCouponException.class , () -> couponService.applyCoupon(1L,cart));
    }

    private static CartDTO createCart(){
        return CartDTO.builder().items(List.of(CartItem.builder().productId(1).quantity(6).price(50).build())).build();
    }

    private static CouponEntity createCouponEntity(){
       return CouponEntity.builder().id(1L).type(CouponTypeEnum.CART).expireDate(LocalDate.now().plusDays(30)).build();
    }

    private static CouponDTO createCartWiseCoupon(){
        CouponDetails couponDetails = CouponDetails.builder()
                .discount(10)
                .threshold(100)
                .build();
         return CouponDTO.builder()
                .type(CouponTypeEnum.CART.getValue())
                .details(couponDetails)
                .build();
    }

}
