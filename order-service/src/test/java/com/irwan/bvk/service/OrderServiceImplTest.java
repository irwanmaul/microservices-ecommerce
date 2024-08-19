package com.irwan.bvk.service;

import com.irwan.bvk.model.Order;
import com.irwan.bvk.service.client.RestClientProductService;
import com.irwan.bvk.dto.CreateOrderRequest;
import com.irwan.bvk.dto.OrderDetailDto;
import com.irwan.bvk.dto.client.InventoryDetailResponse;
import com.irwan.bvk.repository.OrderDetailRepository;
import com.irwan.bvk.repository.OrderRepository;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private RestClientProductService client;

    @Test
    public void whenPlaceOrder_success() {
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
        Map<Integer, InventoryDetailResponse> map = new HashMap<>();
        map.put(1, InventoryDetailResponse.builder()
                        .productId(1)
                        .stock(10)
                        .price(1000)
                        .productName("baju")
                .build());
        when(client.getInventories(any(CreateOrderRequest.class))).thenReturn(map);
        Order order = Order.builder()
                .orderDate(new Timestamp(new Date().getTime()))
                .amount(createOrderRequest.getOrderDetails().stream().mapToDouble(OrderDetailDto::getPrice).sum())
                .build();

        order.setId(1);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.createOrder(createOrderRequest);

        verify(client, times(1)).getInventories(any(CreateOrderRequest.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderDetailRepository, times(1)).saveAll(anyList());
        verify(client, times(1)).updateInventories(anyList());
    }

    @Test
    public void whenPlaceOrder_productNotFound() {
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
        when(client.getInventories(any(CreateOrderRequest.class))).thenReturn(new HashMap<>());
        Order order = Order.builder()
                .orderDate(new Timestamp(new Date().getTime()))
                .amount(createOrderRequest.getOrderDetails().stream().mapToDouble(OrderDetailDto::getPrice).sum())
                .build();

        order.setId(1);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Assertions.assertThrows(ResponseStatusException.class, () -> orderService.createOrder(createOrderRequest), "product not found");

        verify(client, times(1)).getInventories(any(CreateOrderRequest.class));
        verify(orderRepository, times(0)).save(any(Order.class));
        verify(orderDetailRepository, times(0)).saveAll(anyList());
        verify(client, times(0)).updateInventories(anyList());
    }

    @Test
    public void whenPlaceOrder_stockUnavailable() {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder().orderDetails(
                List.of(
                        OrderDetailDto.builder()
                                .productId(1)
                                .price(1000)
                                .quantity(5)
                                .build()
                )
        ).build();

        Map<Integer, InventoryDetailResponse> map = new HashMap<>();
        map.put(1, InventoryDetailResponse.builder()
                .productId(1)
                .stock(1)
                .price(1000)
                .productName("baju")
                .build());

        when(validator.validate(createOrderRequest)).thenReturn(new HashSet<>());
        when(client.getInventories(any(CreateOrderRequest.class))).thenReturn(map);
        Order order = Order.builder()
                .orderDate(new Timestamp(new Date().getTime()))
                .amount(createOrderRequest.getOrderDetails().stream().mapToDouble(OrderDetailDto::getPrice).sum())
                .build();

        order.setId(1);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Assertions.assertThrows(ResponseStatusException.class, () -> orderService.createOrder(createOrderRequest), "stock unavailable");

        verify(client, times(1)).getInventories(any(CreateOrderRequest.class));
        verify(orderRepository, times(0)).save(any(Order.class));
        verify(orderDetailRepository, times(0)).saveAll(anyList());
        verify(client, times(0)).updateInventories(anyList());
    }
}