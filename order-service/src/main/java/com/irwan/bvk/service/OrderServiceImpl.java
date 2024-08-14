package com.irwan.bvk.service;

import com.irwan.bvk.dto.client.InventoryDetailResponse;
import com.irwan.bvk.dto.client.InventoryResponse;
import com.irwan.bvk.dto.CreateOrderRequest;
import com.irwan.bvk.dto.CreateOrderResponse;
import com.irwan.bvk.dto.OrderDetailDto;
import com.irwan.bvk.dto.client.UpdateInventoryRequest;
import com.irwan.bvk.model.Order;
import com.irwan.bvk.model.OrderDetail;
import com.irwan.bvk.repository.OrderDetailRepository;
import com.irwan.bvk.repository.OrderRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Transactional
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Set<ConstraintViolation<CreateOrderRequest>> validate = validator.validate(request);

        if(!validate.isEmpty()) {
            throw new ConstraintViolationException(validate);
        }

        Map<Integer, InventoryDetailResponse> invMap = callInventoryService(request);

        boolean isAllProductAvailable = request.getOrderDetails()
                .stream()
                .allMatch(x -> invMap.get(x.getProductId()) != null);

        if(!isAllProductAvailable) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product not found");
        }

        boolean isStockAvailable = request.getOrderDetails()
                .stream()
                .allMatch(x -> {
                    InventoryDetailResponse invDet = invMap.get(x.getProductId());
                    if(invDet == null) return false;
                    return x.getQuantity() <= invDet.getStock();
                });

        if(!isStockAvailable) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stock unavailable");
        }

        Order order = Order.builder()
                .orderDate(new Timestamp(new Date().getTime()))
                .amount(request.getOrderDetails().stream().mapToDouble(OrderDetailDto::getPrice).sum())
                .build();

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailDto details : request.getOrderDetails()) {
            OrderDetail orderDetailModel = details.toOrderDetailModel();
            orderDetailModel.setOrder(order);
            InventoryDetailResponse detailResponse = invMap.get(details.getProductId());
            orderDetailModel.setProductName(detailResponse.getProductName());

            orderDetails.add(orderDetailModel);
        }

        Order savedOrder = orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);

        callUpdateInventoryService(orderDetails);

        return CreateOrderResponse.builder()
                .orderId(savedOrder.getId())
                .orderDate(savedOrder.getOrderDate())
                .amount(savedOrder.getAmount())
                .orderDetails(request.getOrderDetails())
                .build();
    }

    private Map<Integer, InventoryDetailResponse> callInventoryService(CreateOrderRequest request) {
        Set<Integer> skus = request.getOrderDetails().stream().map(OrderDetailDto::getProductId).collect(Collectors.toSet());

        RestClient productClient = restClientBuilder.baseUrl("http://product-service").build();
        ResponseEntity<InventoryResponse> entity = productClient.get()
                .uri("/api/inventory", uriBuilder -> uriBuilder.queryParam("pid", skus).build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        if(!entity.getStatusCode().equals(HttpStatus.OK)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error create order");
        }

        return Objects.requireNonNull(entity.getBody()).getData()
                .stream()
                .collect(Collectors.toMap(InventoryDetailResponse::getProductId, Function.identity()));
    }

    private void callUpdateInventoryService(List<OrderDetail> orderDetails) {
        RestClient productClient = restClientBuilder.baseUrl("http://product-service").build();

        List<UpdateInventoryRequest> inventoryRequests = orderDetails
                .stream()
                .map(o -> UpdateInventoryRequest.builder()
                        .stockChange(-o.getQuantity())
                        .productId(o.getId())
                        .build()
                ).toList();

        ResponseEntity<Void> entity = productClient.patch()
                .uri("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .body(inventoryRequests)
                .retrieve()
                .toBodilessEntity();
        if(!entity.getStatusCode().equals(HttpStatusCode.valueOf(200))) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error update inventory");
        }
    }
}
