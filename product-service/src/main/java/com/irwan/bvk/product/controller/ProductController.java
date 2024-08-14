package com.irwan.bvk.product.controller;

import com.irwan.bvk.product.dto.ApiResponse;
import com.irwan.bvk.product.dto.RegisterProductRequest;
import com.irwan.bvk.product.dto.RegisterProductResponse;
import com.irwan.bvk.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(
            path = "api/product",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterProductResponse> register(@RequestBody RegisterProductRequest request) {
        RegisterProductResponse registerProductResponse = productService.createProduct(request);

        return ApiResponse.<RegisterProductResponse>builder().data(registerProductResponse).build();
    }
}
