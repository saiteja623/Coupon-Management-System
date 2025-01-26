package com.example.coupon_management.dto;

import com.example.coupon_management.model.CouponDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponDTO {
    @JsonProperty("coupon_id")
    private Long couponId;
    private String type;
    private Integer discount;
    private CouponDetails details;
    @JsonProperty("expire_date")
    private String expireDate;
}
