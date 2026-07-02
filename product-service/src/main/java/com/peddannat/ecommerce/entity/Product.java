package com.peddannat.ecommerce.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity mapped to products table.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    private String imageUrl;

    @Column(nullable = false)
    // Soft delete flag; false means hidden/inactive
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    // Created timestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    // Updated timestamp
    private LocalDateTime updatedAt;



    @PrePersist
    public void prePersist() {
        // Set timestamps before first save
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.active = true;
    }

    @PreUpdate
    public void preUpdate() {
        // Update timestamp on every modification
        this.updatedAt = LocalDateTime.now();
    }


}
