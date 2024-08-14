package com.irwan.bvk.dto.client;

import lombok.Data;

import java.util.List;

@Data
public class InventoryResponse {
    List<InventoryDetailResponse> data;
}
