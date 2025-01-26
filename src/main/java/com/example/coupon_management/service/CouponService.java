package com.example.coupon_management.service;

import com.example.coupon_management.dto.CartDTO;
import com.example.coupon_management.dto.CouponDTO;
import com.example.coupon_management.entity.CouponEntity;
import com.example.coupon_management.exception.CouponNotFoundException;
import com.example.coupon_management.exception.InvalidCouponException;
import com.example.coupon_management.model.CouponDetails;
import com.example.coupon_management.model.CouponTypeEnum;
import com.example.coupon_management.repository.CouponRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DiscountHelperService discountHelperService;
    @Autowired
    private CouponManagerService couponManagerService;

    public CouponDTO createCoupon(CouponDTO couponDTO) {
        if (!CouponTypeEnum.isValidCoupon(couponDTO.getType()))
            throw new InvalidCouponException("Invalid coupon type");
        Integer discount  = couponDTO.getDetails().getDiscount();
        if(discount!=null && discount <= 0){
            throw new IllegalArgumentException("Discount should not be negative");
        }
        CouponEntity couponEntity = couponDTOtoEntity(couponDTO);
        return couponEntityToDTO(couponRepository.save(couponEntity));
    }

    public List<CouponDTO> getAllCoupons() {
        List<CouponEntity> couponEntities = couponRepository.findAll();
        List<CouponDTO> availableCoupons = new ArrayList<>();
        couponEntities.forEach(couponEntity -> {
            availableCoupons.add(couponEntityToDTO(couponEntity));
        });
        return availableCoupons;
    }

    public CouponDTO getCouponById(Long id) {
        CouponEntity couponEntity = findCouponEntityById(id);
        return couponEntityToDTO(couponEntity);
    }


    public CouponDTO updateCouponById(Long id, CouponDTO couponDTO) {
        CouponDTO updatedCoupon = null;
        try {
            if (!CouponTypeEnum.isValidCoupon(couponDTO.getType()))
                throw new InvalidCouponException("Invalid coupon type");
            CouponEntity currentCoupon = findCouponEntityById(id);
            currentCoupon.setType(currentCoupon.getType());
            currentCoupon.setDetails(objectMapper.writeValueAsString(couponDTO.getDetails()));
            updatedCoupon = couponEntityToDTO(couponRepository.save(currentCoupon));
        } catch (JsonProcessingException ex) {
            log.error("Error while updating coupon Id {} ", id);
            return null;
        }
        return updatedCoupon;
    }

    public void deleteCouponById(Long id) {
        CouponEntity couponEntity = findCouponEntityById(id);
        couponRepository.delete(couponEntity);
    }

    public List<CouponDTO> getApplicableCoupons(CartDTO cart) {
        List<CouponDTO> applicableCoupons = new ArrayList<>();
        List<CouponDTO> coupons = getAllCoupons();
        coupons.forEach(couponDTO -> {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate expireDateTime = LocalDate.parse(couponDTO.getExpireDate(), dateTimeFormatter);
            if (expireDateTime.isAfter(LocalDate.now())) {
                int discount = discountHelperService.getCouponDiscount(couponDTO, cart);
                if (discount > 0) {
                    applicableCoupons.add(CouponDTO.builder()
                            .couponId(couponDTO.getCouponId())
                            .type(couponDTO.getType())
                            .discount(discount)
                            .build());
                }
            }
        });
        return applicableCoupons;
    }

    public CartDTO applyCoupon(Long id, CartDTO cart) {
        CouponDTO coupon = getCouponById(id);
        if(isCouponExpired(coupon)){
            throw new InvalidCouponException("Coupon Expired and cannot be applied!");
        }
        return couponManagerService.applyCoupon(coupon, cart);
    }

    private CouponDTO couponEntityToDTO(CouponEntity couponEntity) {
        CouponDTO couponDTO = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            couponDTO = CouponDTO.builder()
                    .couponId(couponEntity.getId())
                    .type(couponEntity.getType().getValue())
                    .details(objectMapper.readValue(couponEntity.getDetails(), CouponDetails.class))
                    .expireDate(couponEntity.getExpireDate().format(formatter))
                    .build();
        } catch (JsonProcessingException ex) {
            log.error("Error converting Coupon Entity to DTO" + ex.getMessage());
        }
        return couponDTO;
    }

    private CouponEntity couponDTOtoEntity(CouponDTO couponDTO) {
        CouponEntity couponEntity = CouponEntity.builder().build();
        try {
            couponEntity.setType(CouponTypeEnum.getCouponByType(couponDTO.getType()));
            couponEntity.setDetails(objectMapper.writeValueAsString(couponDTO.getDetails()));
            LocalDate expireDate;
            if (couponDTO.getExpireDate() != null && !couponDTO.getExpireDate().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                expireDate = LocalDate.parse(couponDTO.getExpireDate(), formatter);
            } else {
                expireDate = LocalDate.now().plusDays(30);
            }
            couponEntity.setExpireDate(expireDate);
        } catch (JsonProcessingException ex) {
            log.error("Error converting Coupon  DTO To Entity " + ex.getMessage());
        }
        return couponEntity;
    }


    private CouponEntity findCouponEntityById(Long id) {
        Optional<CouponEntity> optionalCouponEntity = couponRepository.findById(id);
        if (optionalCouponEntity.isEmpty()) {
            throw new CouponNotFoundException("Coupon Not found!");
        }
        return optionalCouponEntity.get();
    }

    private boolean isCouponExpired(CouponDTO couponDTO){
        if(couponDTO.getExpireDate()!=null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate expireDate = LocalDate.parse(couponDTO.getExpireDate(), formatter);
            return LocalDate.now().isAfter(expireDate);
        }
        return false;
    }


}
