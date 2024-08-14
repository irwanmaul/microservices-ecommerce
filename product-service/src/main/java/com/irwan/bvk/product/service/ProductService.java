package com.irwan.bvk.product.service;

import com.irwan.bvk.product.dto.RegisterProductRequest;

public interface ProductService {
    void createProduct(RegisterProductRequest productRequest);
}
