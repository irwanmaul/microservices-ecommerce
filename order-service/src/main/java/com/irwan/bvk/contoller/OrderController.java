package com.irwan.bvk.contoller;

import com.irwan.bvk.dto.ApiResponse;
import com.irwan.bvk.dto.CreateOrderRequest;
import com.irwan.bvk.dto.CreateOrderResponse;
import com.irwan.bvk.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(path = "/api/order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateOrderResponse> placeOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResponse orderResponse = orderService.createOrder(request);

        return ApiResponse.<CreateOrderResponse>builder().data(orderResponse).build();
    }
}
