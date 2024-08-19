package com.irwan.bvk.service.client;

import com.irwan.bvk.dto.ApiResponse;
import com.irwan.bvk.dto.CreateOrderRequest;
import com.irwan.bvk.dto.OrderDetailDto;
import com.irwan.bvk.dto.client.InventoryDetailResponse;
import com.irwan.bvk.dto.client.UpdateInventoryRequest;
import com.irwan.bvk.model.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestClientProductService {

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Value("${service.product.base-url}")
    private String productServiceUrl;

    public Map<Integer, InventoryDetailResponse> getInventories(CreateOrderRequest request) {
        Set<Integer> skus = request.getOrderDetails().stream().map(OrderDetailDto::getProductId).collect(Collectors.toSet());

        RestClient productClient = restClientBuilder.baseUrl(productServiceUrl).build();
        ResponseEntity<ApiResponse<List<InventoryDetailResponse>>> entity = productClient.get()
                .uri("/api/inventory", uriBuilder -> uriBuilder.queryParam("pid", skus).build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        if(!entity.getStatusCode().equals(HttpStatus.OK)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error create order");
        }

        log.info("Response Entity {}", entity);

        return Objects.requireNonNull(entity.getBody()).getData()
                .stream()
                .collect(Collectors.toMap(InventoryDetailResponse::getProductId, Function.identity()));
    }

    public void updateInventories(List<OrderDetail> orderDetails) {
        RestClient productClient = restClientBuilder.baseUrl(productServiceUrl).build();

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
