package com.irwan.bvk.product.service;

import com.github.slugify.Slugify;
import com.irwan.bvk.product.dto.RegisterProductRequest;
import com.irwan.bvk.product.model.Inventory;
import com.irwan.bvk.product.model.Product;
import com.irwan.bvk.product.repository.InventoryRepository;
import com.irwan.bvk.product.repository.ProductRepository;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private Validator validator;

    @Test
    void registerProductSuccess() {
        Product product = Product.builder()
                .productName("baju")
                .slug(Slugify.builder().build().slugify("baju"))
                .build();

        RegisterProductRequest registerProductRequest = RegisterProductRequest.builder().name("baju").price(1000).stock(10).build();

        when(validator.validate(registerProductRequest)).thenReturn(new HashSet<>());
        when(productRepository.findBySlug("baju")).thenReturn(new ArrayList<>());
        when(productRepository.save(product)).thenReturn(product);
        Inventory inventory = Inventory.builder()
                .product(product)
                .stock(10)
                .price(1000)
                .build();
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        productService.createProduct(registerProductRequest);
    }

    @Test
    void registerProductFailed_whenSlugExist() {
        Product product = Product.builder()
                .productName("baju")
                .slug(Slugify.builder().build().slugify("baju"))
                .build();

        RegisterProductRequest registerProductRequest = RegisterProductRequest.builder().name("baju").price(1000).stock(10).build();

        when(validator.validate(registerProductRequest)).thenReturn(new HashSet<>());
        when(productRepository.findBySlug("baju")).thenReturn(Collections.singletonList(product));

        assertThrows(ResponseStatusException.class, () -> productService.createProduct(registerProductRequest));
    }
}