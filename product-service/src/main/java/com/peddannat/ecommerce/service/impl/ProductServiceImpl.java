package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.dto.request.ProductRequest;
import com.peddannat.ecommerce.dto.response.ProductResponse;
import com.peddannat.ecommerce.entity.Product;
import com.peddannat.ecommerce.exception.ResourceNotFoundException;
import com.peddannat.ecommerce.repository.ProductRepository;
import com.peddannat.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {

        Product product = new Product();

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setActive(true);

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);

    }

//    @Override
//    public List<ProductResponse> getAllProducts() {
//        return productRepository.findByActiveTrue()
//                .stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }


    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + id)
        );
        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + id)
        );

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setCategory(productRequest.getCategory());
        existingProduct.setImageUrl(productRequest.getImageUrl());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + id)
        );

        // Soft delete product instead of removing it permanently from the database
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndActiveTrue(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.isActive(),
                product.getCreatedAt()
        );
    }

}
