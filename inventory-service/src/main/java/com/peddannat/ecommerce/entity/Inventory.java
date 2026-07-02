package com.peddannat.ecommerce.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    // Each product should have only one inventory record
    private Long productId;

    @Column(nullable = false)
    // Total quantity available in stock system
    private int quantity;

    @Column(nullable = false)
    // Quantity currently reserved by active orders
    private int reservedQuantity;

    @Column(nullable = false)
    // Last update timestamp
    private LocalDateTime lastUpdated;

    /**
     * Calculates available quantity.
     * Formula = total quantity - reserved quantity.
     */
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        // Automatically updates timestamp on insert and update
        this.lastUpdated = LocalDateTime.now();
    }

}
