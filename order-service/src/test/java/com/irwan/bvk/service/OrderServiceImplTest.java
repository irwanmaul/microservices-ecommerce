package com.irwan.bvk.service;

import com.irwan.bvk.dto.CreateOrderRequest;
import com.irwan.bvk.dto.CreateOrderResponse;
import com.irwan.bvk.dto.OrderDetailDto;
import com.irwan.bvk.dto.client.InventoryResponse;
import com.irwan.bvk.model.Order;
import com.irwan.bvk.model.OrderDetail;
import com.irwan.bvk.repository.OrderDetailRepository;
import com.irwan.bvk.repository.OrderRepository;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private Validator validator;

    @Mock
    private RestClient.Builder restClientBuilder;

    private RestClient restClient;
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec uri;
    private RestClient.ResponseSpec retrieve;
    private ResponseEntity entity;
    private ResponseEntity<Void> bodilessEntity;

    /*patch*/
    private RestClient.RequestBodyUriSpec patchRequestHeadersUriSpec;

    @BeforeEach
    void setUpCallInventory() {
        restClient = Mockito.mock(RestClient.class);
        requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        uri = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        retrieve = Mockito.mock(RestClient.ResponseSpec.class);
        entity = Mockito.mock(ResponseEntity.class);
        bodilessEntity = Mockito.mock(ResponseEntity.class);

        /*get*/
        Mockito.lenient().when(restClientBuilder.baseUrl("http://product-service")).thenReturn(restClientBuilder);
        Mockito.lenient().when(restClientBuilder.build()).thenReturn(restClient);
        Mockito.lenient().when(restClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.lenient().when(requestHeadersUriSpec.uri("/api/inventory",
                uriBuilder -> {
                    UriBuilder builder = (UriBuilder) uriBuilder;
                    return builder.queryParam("pid", List.of(1)).build();
                }))
                .thenReturn(uri);
        Mockito.lenient().when(uri.retrieve()).thenReturn(retrieve);
        Mockito.lenient().when(retrieve.toEntity(new ParameterizedTypeReference<>() {})).thenReturn(entity);

        /*patch*/
//        Mockito.lenient().when(restClientBuilder.baseUrl("http://product-service")).thenReturn(restClientBuilder);
//        Mockito.lenient().when(restClientBuilder.build()).thenReturn(restClient);
//        Mockito.lenient().when(restClient.patch()).thenReturn(patchRequestHeadersUriSpec);
//        Mockito.lenient().when(requestHeadersUriSpec.uri("/api/inventory")).thenReturn(uri);
//        Mockito.lenient().when(uri.retrieve()).thenReturn(retrieve);
//        Mockito.lenient().when(retrieve.toBodilessEntity()).thenReturn(bodilessEntity);
    }

    @Test
    void successCreateOrder() {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder().orderDetails(
                List.of(
                        OrderDetailDto.builder()
                                .productId(1)
                                .price(1000)
                                .quantity(1)
                                .build()
                )
        ).build();


        when(validator.validate(createOrderRequest)).thenReturn(new HashSet<>());
        when(entity.getStatusCode()).thenReturn(HttpStatus.OK);
        Order order = Order.builder()
                .orderDate(new Timestamp(new Date().getTime()))
                .status("BOOKED")
                .amount(createOrderRequest.getOrderDetails().stream().mapToDouble(OrderDetailDto::getPrice).sum())
                .build();

        Order savedOrder = order;
        savedOrder.setId(1);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(bodilessEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        log.info("restClient {}", restClient);
        log.info("requestHeadersUriSpec {}", requestHeadersUriSpec);
        log.info("uri {}", uri);
        log.info("retrieve {}", retrieve);
        log.info("entity {}", entity);

        /*CreateOrderResponse orderResponse = orderService.createOrder(createOrderRequest);

        verify(orderRepository.save(order), Mockito.times(1));
        verify(orderDetailRepository.saveAll(new ArrayList<>()), Mockito.times(1));

        assertEquals(orderResponse.getOrderId(), 1);*/
    }
}