package com.example.coupon_management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponDetails {
    private Integer threshold;
    private Integer discount;
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("buy_products")
    private List<Product> buyProducts;
    @JsonProperty("get_products")
    private List<Product> getProducts;
    @JsonProperty("repetition_limit")
    private Integer repetitionLimit;
}
