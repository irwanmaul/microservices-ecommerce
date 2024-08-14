package com.irwan.bvk.product.service;

import com.irwan.bvk.product.dto.GetInventoryResponse;
import com.irwan.bvk.product.dto.UpdateStockRequest;
import com.irwan.bvk.product.model.Inventory;
import com.irwan.bvk.product.repository.InventoryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService{

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private Validator validator;

    @Override
    public List<GetInventoryResponse> checkAvailability(List<Integer> productNumbers) {
        List<Inventory> inventories = inventoryRepository.findByProductIdIn(productNumbers);

        return inventories.stream().map(InventoryServiceImpl::castToDto).toList();
    }

    @Transactional
    @Override
    public void updateStock(List<UpdateStockRequest> list) {
        for (UpdateStockRequest updateStock : list) {
            Set<ConstraintViolation<UpdateStockRequest>> constraintViolations = validator.validate(updateStock);
            if(!constraintViolations.isEmpty()) {
                throw new ConstraintViolationException(constraintViolations);
            }
        }

        List<Integer> skus = list.stream().map(UpdateStockRequest::getProductId).collect(Collectors.toList());

        List<Inventory> inventoryDb = inventoryRepository.findByProductIdIn(skus);

        for (UpdateStockRequest updateStockRequest : list) {
            Inventory inventory = inventoryDb.stream()
                    .filter(x -> updateStockRequest.getProductId().equals(x.getProduct().getId()))
                    .findAny().orElse(null);

            if(inventory != null) {
                inventory.setStock(inventory.getStock() + updateStockRequest.getStockChange());
                inventoryRepository.save(inventory);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("product %s not found", updateStockRequest.getProductId()));
            }
        }
    }

    private static GetInventoryResponse castToDto(Inventory inventory) {
        return GetInventoryResponse.builder()
                .price(inventory.getPrice())
                .stock(inventory.getStock())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getProductName())
                .build();
    }
}
