package com.irwan.bvk.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GetInventoryResponse {
    private int productId;
    private String productName;
    private int stock;
    private double price;
}
