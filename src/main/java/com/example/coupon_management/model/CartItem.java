package com.example.coupon_management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItem {
    @JsonProperty("product_id")
    private  Integer productId;
    private Integer quantity;
    private Integer price;
    @JsonProperty("total_discount")
    private int totalDiscount;
}
