package com.example.coupon_management.dto;

import com.example.coupon_management.model.CartItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO {
    @JsonProperty("items")
    private List<CartItem> items;
    @JsonProperty("total_price")
    private Integer totalPrice;
    @JsonProperty("total_discount")
    private Integer totalDiscount;
    @JsonProperty("final_price")
    private Integer finalPrice;
}
