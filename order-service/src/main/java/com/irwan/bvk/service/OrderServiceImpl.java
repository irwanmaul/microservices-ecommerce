package com.irwan.bvk.service;

import com.irwan.bvk.service.client.RestClientProductService;
import com.irwan.bvk.dto.client.InventoryDetailResponse;
import com.irwan.bvk.dto.CreateOrderRequest;
import com.irwan.bvk.dto.CreateOrderResponse;
import com.irwan.bvk.dto.OrderDetailDto;
import com.irwan.bvk.model.Order;
import com.irwan.bvk.model.OrderDetail;
import com.irwan.bvk.repository.OrderDetailRepository;
import com.irwan.bvk.repository.OrderRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.*;

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
    private RestClientProductService restClientProductService;

    @Transactional
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Set<ConstraintViolation<CreateOrderRequest>> validate = validator.validate(request);

        if(!validate.isEmpty()) {
            throw new ConstraintViolationException(validate);
        }

        Map<Integer, InventoryDetailResponse> invMap = restClientProductService.getInventories(request);

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
            orderDetailModel.setProductId(detailResponse.getProductId());

            orderDetails.add(orderDetailModel);
        }

        Order savedOrder = orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);

        restClientProductService.updateInventories(orderDetails);

        return CreateOrderResponse.builder()
                .orderId(savedOrder.getId())
                .orderDate(savedOrder.getOrderDate())
                .amount(savedOrder.getAmount())
                .orderDetails(request.getOrderDetails())
                .build();
    }
}
