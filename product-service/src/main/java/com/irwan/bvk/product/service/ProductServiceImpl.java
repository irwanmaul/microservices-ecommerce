package com.irwan.bvk.product.service;

import com.github.slugify.Slugify;
import com.irwan.bvk.product.dto.RegisterProductRequest;
import com.irwan.bvk.product.dto.RegisterProductResponse;
import com.irwan.bvk.product.model.Inventory;
import com.irwan.bvk.product.model.Product;
import com.irwan.bvk.product.repository.InventoryRepository;
import com.irwan.bvk.product.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private Validator validator;

    @Transactional
    @Override
    public RegisterProductResponse createProduct(RegisterProductRequest productRequest) {

        Set<ConstraintViolation<RegisterProductRequest>> constraintViolations = validator.validate(productRequest);
        if(!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }

        String slug = Slugify.builder().build().slugify(productRequest.getName());
        if(!productRepository.findBySlug(slug).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product Name Already Registered");
        }

        Product product = Product.builder()
                .productName(productRequest.getName())
                .slug(slug)
                .build();

        Product saved = productRepository.save(product);
        Inventory savedInventory = inventoryRepository.save(Inventory.builder().price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .product(saved)
                .build());

        return RegisterProductResponse.builder()
                .productId(saved.getId())
                .productName(saved.getProductName())
                .slug(saved.getSlug())
                .stock(savedInventory.getStock())
                .price(savedInventory.getPrice())
                .build();
    }
}
