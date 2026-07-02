package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.dto.request.InventoryRequest;
import com.peddannat.ecommerce.dto.response.InventoryResponse;
import com.peddannat.ecommerce.entity.Inventory;
import com.peddannat.ecommerce.exception.InsufficientStockException;
import com.peddannat.ecommerce.exception.ResourceNotFoundException;
import com.peddannat.ecommerce.repository.InventoryRepository;
import com.peddannat.ecommerce.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Inventory service implementation containing business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;


    @Override
    public InventoryResponse createInventory(InventoryRequest request) {

        log.info("Creating inventory for productId={}", request.getProductId());

        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException("Inventory already exists for product id: " + request.getProductId());
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(request.getProductId());
        inventory.setQuantity(request.getQuantity());
        inventory.setReservedQuantity(request.getReservedQuantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        log.info("Inventory created successfully for productId={}", request.getProductId());
        return mapToResponse(savedInventory);
    }

    @Override
    public InventoryResponse getInventoryByProductId(Long productId) {

        log.info("Fetching inventory for productId={}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));

        return mapToResponse(inventory);

    }

    @Override
    @Transactional
    public InventoryResponse addStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToResponse(savedInventory);
    }

    @Override
    @Transactional
    public InventoryResponse reduceStock(Long productId, int quantity) {

        log.info("Adding stock. productId={}, quantity={}", productId, quantity);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be greater than 0");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product id: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);

        if (inventory.getReservedQuantity() > inventory.getQuantity()) {
            throw new IllegalArgumentException("Reserved quantity cannot be greater than total quantity after stock reduction");
        }


        Inventory savedInventory = inventoryRepository.save(inventory);

        log.info("Stock reduced successfully. productId={}, newQuantity={}", productId, savedInventory.getQuantity());

        return mapToResponse(savedInventory);
    }


    @Override
    public boolean checkStock(Long productId, int quantity) {

        log.info("Checking stock. productId={}, requestedQuantity={}", productId, quantity);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to check must be greater than 0");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));

        return inventory.getAvailableQuantity() >= quantity;
    }

    /**
     * Converts Inventory entity to InventoryResponse DTO.
     */
    private InventoryResponse mapToResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                inventory.getAvailableQuantity(),
                inventory.getLastUpdated()
        );
    }
}
