package com.irwan.bvk.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStockRequest {

    @NotNull
    private Integer productId;

    @NotNull
    private Integer stockChange;
}
