package com.irwan.bvk.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InventoryDetailResponse {
    private int productId;
    private String productName;
    private int stock;
    private double price;
}
