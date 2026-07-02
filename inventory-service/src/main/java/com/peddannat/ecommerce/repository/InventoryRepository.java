package com.peddannat.ecommerce.repository;


import com.peddannat.ecommerce.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    // Finds inventory record by product id
    Optional<Inventory> findByProductId(Long productId);

    // Checks whether inventory already exists for a product
    boolean existsByProductId(Long productId);

}
