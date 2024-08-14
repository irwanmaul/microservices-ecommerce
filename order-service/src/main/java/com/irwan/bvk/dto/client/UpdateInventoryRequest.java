package com.irwan.bvk.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdateInventoryRequest {
    private Integer productId;

    private Integer stockChange;
}
