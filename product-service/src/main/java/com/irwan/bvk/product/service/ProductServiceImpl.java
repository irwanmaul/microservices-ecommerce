package com.irwan.bvk.product.service;

import com.github.slugify.Slugify;
import com.irwan.bvk.product.dto.RegisterProductRequest;
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

    @Override
    public void createProduct(RegisterProductRequest productRequest) {

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
        inventoryRepository.save(Inventory.builder().price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .product(saved)
                .build());
    }
}
