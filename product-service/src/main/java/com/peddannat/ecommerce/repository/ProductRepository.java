package com.peddannat.ecommerce.repository;

import com.peddannat.ecommerce.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByCategory(String category);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryAndActiveTrue(String category);

    Page<Product> findByActiveTrue(Pageable pageable);



}
