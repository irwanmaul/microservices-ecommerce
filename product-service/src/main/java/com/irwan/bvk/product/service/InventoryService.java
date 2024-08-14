package com.irwan.bvk.product.service;

import com.irwan.bvk.product.dto.GetInventoryResponse;
import com.irwan.bvk.product.dto.UpdateStockRequest;

import java.util.List;

public interface InventoryService {
    List<GetInventoryResponse> checkAvailability(List<Integer> productNumbers);

    void updateStock(List<UpdateStockRequest> list);

}
