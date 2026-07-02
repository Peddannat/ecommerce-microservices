package com.peddannat.ecommerce.repository;
import com.peddannat.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for order persistence operations.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Fetches orders by user sorted by newest first with items eagerly loaded
    @EntityGraph(attributePaths = "items")
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    // Fetches all orders with items eagerly loaded
    @Override
    @EntityGraph(attributePaths = "items")
    List<Order> findAll();


    // Fetches order by id with items eagerly loaded to avoid N+1
    @EntityGraph(attributePaths = "items")
    Optional<Order> findById(Long id);
}
