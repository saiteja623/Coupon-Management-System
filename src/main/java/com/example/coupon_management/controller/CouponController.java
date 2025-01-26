package com.example.coupon_management.controller;

import com.example.coupon_management.dto.CartDTO;
import com.example.coupon_management.dto.CouponDTO;
import com.example.coupon_management.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/coupons")
    public ResponseEntity<CouponDTO> createCoupon(@RequestBody @Valid CouponDTO couponDTO){
        CouponDTO createdCouponDTO = couponService.createCoupon(couponDTO);
        return ResponseEntity.ok(createdCouponDTO);
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponDTO>> getAllCoupons(){
        List<CouponDTO> availableCoupons = couponService.getAllCoupons();
        return ResponseEntity.status(HttpStatus.OK).body(availableCoupons);
    }

    @GetMapping("/coupons/{id}")
    public ResponseEntity<CouponDTO> getCouponById(@PathVariable Long id){
        CouponDTO coupon = couponService.getCouponById(id);
        return ResponseEntity.status(HttpStatus.OK).body(coupon);
    }

    @PutMapping("/coupons/{id}")
    public  ResponseEntity<CouponDTO> updateCouponById(@PathVariable Long id,@RequestBody CouponDTO couponDTO){
        return ResponseEntity.status(HttpStatus.OK).body(couponService.updateCouponById(id,couponDTO));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<String> deleteCouponById(@PathVariable Long id){
        couponService.deleteCouponById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Coupon deleted Successfully!");
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<CouponDTO>> getApplicableCoupons(@RequestBody CartDTO cart){
        return ResponseEntity.status(HttpStatus.OK).body(couponService.getApplicableCoupons(cart));
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<CartDTO> applyCoupon(@PathVariable Long id,@RequestBody CartDTO cart){
        return ResponseEntity.ok(couponService.applyCoupon(id,cart));
    }
}
