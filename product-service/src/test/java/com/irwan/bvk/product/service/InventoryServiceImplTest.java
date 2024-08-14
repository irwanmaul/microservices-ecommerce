package com.irwan.bvk.product.service;

import com.github.slugify.Slugify;
import com.irwan.bvk.product.dto.GetInventoryResponse;
import com.irwan.bvk.product.dto.UpdateStockRequest;
import com.irwan.bvk.product.model.Inventory;
import com.irwan.bvk.product.model.Product;
import com.irwan.bvk.product.repository.InventoryRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private Validator validator;

    List<Integer> productNumbers = List.of(1, 2, 3);
    List<Inventory> inventories = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (Integer productNumber : productNumbers) {
            inventories.add(
                    Inventory.builder().product(
                                    Product.builder()
                                            .productName("product " + productNumber)
                                            .id(productNumber)
                                            .slug(Slugify.builder().build().slugify("product " + productNumber))
                                            .build()
                            )
                            .stock(100)
                            .price(1000)
                            .build()
            );
        }
    }

    @Test
    void checkAvailability_success() {

        when(inventoryRepository.findByProductIdIn(productNumbers)).thenReturn(inventories);

        List<GetInventoryResponse> getInventoryResponses = inventoryService.checkAvailability(productNumbers);
        assertEquals(3, getInventoryResponses.size());
    }

    @Test
    void updateStock_success() {
        List<UpdateStockRequest> updateStockRequests = new ArrayList<>();
        updateStockRequests.add(UpdateStockRequest.builder()
                .productId(1)
                .stockChange(10)
                .build()
        );
        updateStockRequests.add(UpdateStockRequest.builder()
                .productId(2)
                .stockChange(-10)
                .build()
        );
        updateStockRequests.add(UpdateStockRequest.builder()
                .productId(3)
                .stockChange(5)
                .build()
        );
        when(validator.validate(updateStockRequests.get(0))).thenReturn(new HashSet<>());
        when(validator.validate(updateStockRequests.get(1))).thenReturn(new HashSet<>());
        when(validator.validate(updateStockRequests.get(2))).thenReturn(new HashSet<>());
        when(inventoryRepository.findByProductIdIn(this.productNumbers)).thenReturn(inventories);
        for (Inventory inventory : inventories) {
            when(inventoryRepository.save(inventory)).thenReturn(inventory);
        }

        inventoryService.updateStock(updateStockRequests);
        for (Inventory inventory : inventories) {
            verify(inventoryRepository, Mockito.times(1)).save(inventory);
        }
        verify(inventoryRepository, Mockito.times(1)).findByProductIdIn(this.productNumbers);
    }

    @Test
    void updateStock_failedWhenProductNotFound() {
        List<UpdateStockRequest> updateStockRequests = new ArrayList<>();
        updateStockRequests.add(UpdateStockRequest.builder()
                .productId(1)
                .stockChange(10)
                .build()
        );

        when(inventoryRepository.findByProductIdIn(Collections.singletonList(1))).thenReturn(new ArrayList<>());
        assertThrows(ResponseStatusException.class, () -> inventoryService.updateStock(updateStockRequests));
    }
}