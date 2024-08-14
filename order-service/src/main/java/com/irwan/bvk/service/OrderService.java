package com.irwan.bvk.service;

import com.irwan.bvk.dto.CreateOrderRequest;
import com.irwan.bvk.dto.CreateOrderResponse;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request);
}
