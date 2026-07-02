package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.dto.request.ProductRequest;
import com.peddannat.ecommerce.dto.response.ProductResponse;
import com.peddannat.ecommerce.entity.Product;
import com.peddannat.ecommerce.exception.ResourceNotFoundException;
import com.peddannat.ecommerce.repository.ProductRepository;
import com.peddannat.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product service implementation containing business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {

        log.info("Creating product with name: {}", productRequest.getName());

        Product product = new Product();

        product.setName(productRequest.getName().trim());
        product.setDescription(productRequest.getDescription().trim());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory().trim());
        product.setImageUrl(productRequest.getImageUrl());
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());
        return mapToResponse(savedProduct);

    }



    @Override
    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String sortDirection) {
        // Decide sorting direction dynamically
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        log.info("Fetching active products. page={}, size={}, sortBy={}, sortDirection={}",
                page, size, sortBy, sortDirection);

        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }


    @Override
    public ProductResponse getProductById(Long id) {

        log.info("Fetching product by id: {}", id);

        Product product = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {

        log.info("Updating product with id: {}", id);

        Product existingProduct = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id)
        );

        existingProduct.setName(productRequest.getName().trim());
        existingProduct.setDescription(productRequest.getDescription().trim());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setCategory(productRequest.getCategory().trim());
        existingProduct.setImageUrl(productRequest.getImageUrl());

        Product updatedProduct = productRepository.save(existingProduct);

        log.info("Product updated successfully with id: {}", updatedProduct.getId());
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        log.info("Soft deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + id)
        );

        // Soft delete means record stays in DB but becomes inactive
        product.setActive(false);
        productRepository.save(product);

        log.info("Product soft deleted successfully with id: {}", id);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {

        log.info("Fetching products by category: {}", category);

        return productRepository.findByCategoryIgnoreCaseAndActiveTrue(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /**
     * Converts Product entity into API response DTO.
     */
    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

}
