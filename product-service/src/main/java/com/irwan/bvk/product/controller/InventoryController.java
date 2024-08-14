package com.irwan.bvk.product.controller;

import com.irwan.bvk.product.dto.ApiResponse;
import com.irwan.bvk.product.dto.GetInventoryResponse;
import com.irwan.bvk.product.dto.UpdateStockRequest;
import com.irwan.bvk.product.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping(path = "/api/inventory")
    public ApiResponse<List<GetInventoryResponse>> checkAvailability(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] pids = parameterMap.get("pid");
        List<GetInventoryResponse> getInventoryResponse = inventoryService.checkAvailability(Arrays.stream(pids).map(Integer::parseInt).toList());

        return ApiResponse.<List<GetInventoryResponse>>builder().data(getInventoryResponse).build();
    }

    @PatchMapping(path = "/api/inventory")
    public ApiResponse<String> updateStock(@RequestBody List<UpdateStockRequest> request) {

        inventoryService.updateStock(request);

        return ApiResponse.<String>builder().data("OK").build();
    }
}
