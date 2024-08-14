package com.irwan.bvk.product.service;

import com.irwan.bvk.product.dto.RegisterProductRequest;
import com.irwan.bvk.product.dto.RegisterProductResponse;

public interface ProductService {
    RegisterProductResponse createProduct(RegisterProductRequest productRequest);
}
