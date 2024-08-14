package com.irwan.bvk.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterProductResponse {
    private int productId;
    private String productName;
    private String slug;
    private int stock;
    private double price;
}
