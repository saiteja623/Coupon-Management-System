package com.example.coupon_management.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    @JsonProperty("product_id")
    private  Integer productId;
    private Integer quantity;
    private Integer price;
}
