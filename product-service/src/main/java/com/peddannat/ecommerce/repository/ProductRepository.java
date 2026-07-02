package com.peddannat.ecommerce.repository;

import com.peddannat.ecommerce.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    // Returns all active products with pagination
    Page<Product> findByActiveTrue(Pageable pageable);

    // Returns active products by category ignoring case
    List<Product> findByCategoryIgnoreCaseAndActiveTrue(String category);

}
