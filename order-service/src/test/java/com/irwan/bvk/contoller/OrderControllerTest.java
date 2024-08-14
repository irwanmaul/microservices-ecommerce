package com.irwan.bvk.contoller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irwan.bvk.dto.ApiResponse;
import com.irwan.bvk.dto.CreateOrderRequest;
import com.irwan.bvk.dto.CreateOrderResponse;
import com.irwan.bvk.dto.OrderDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void placeOrder() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .orderDetails(
                        List.of(OrderDetailDto.builder()
                                .productId(1)
                                .price(1000)
                                .quantity(1)
                                .build())
                ).build();
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/order")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            ApiResponse<CreateOrderResponse> apiResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            log.info(apiResponse.toString());
            assertNotNull(apiResponse.getData());
        });
    }

    @Test
    void placeOrderFailed_ProductNotFound() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .orderDetails(
                        List.of(OrderDetailDto.builder()
                                .productId(1282832342)
                                .price(1000)
                                .quantity(1)
                                .build())
                ).build();
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/order")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            ApiResponse<CreateOrderResponse> apiResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            log.info(apiResponse.getErrors());
            assertEquals(apiResponse.getErrors(), "product not found");
        });
    }

    @Test
    void placeOrderFailed_StockUnAvailable() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .orderDetails(
                        List.of(OrderDetailDto.builder()
                                .productId(1)
                                .price(10000)
                                .quantity(9999999)
                                .build())
                ).build();
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/order")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            ApiResponse<CreateOrderResponse> apiResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            log.info(apiResponse.getErrors());
            assertEquals(apiResponse.getErrors(), "stock unavailable");
        });
    }
}