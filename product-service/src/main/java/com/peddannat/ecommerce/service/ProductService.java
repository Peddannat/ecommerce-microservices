package com.peddannat.ecommerce.service;

import com.peddannat.ecommerce.dto.request.ProductRequest;
import com.peddannat.ecommerce.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

//    List<ProductResponse> getAllProducts();

    Page<ProductResponse> getAllProducts(int page, int size, String sortBy);


    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);

    List<ProductResponse> getProductsByCategory(String category);

}
