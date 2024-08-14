package com.irwan.bvk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CreateOrderResponse {
    private int orderId;
    private Date orderDate;
    private double amount;

    List<OrderDetailDto> orderDetails;
}
