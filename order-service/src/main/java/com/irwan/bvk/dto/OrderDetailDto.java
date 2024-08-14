package com.irwan.bvk.dto;

import com.irwan.bvk.model.OrderDetail;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDto {
    @Min(1)
    private int productId;

    @Min(1)
    private int quantity;

    @Min(1)
    private double price;

    public OrderDetail toOrderDetailModel() {
        return OrderDetail.builder()
                .price(this.price)
                .quantity(this.quantity)
                .build();
    }
}
