package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.dto.request.InventoryRequest;
import com.peddannat.ecommerce.dto.response.InventoryResponse;
import com.peddannat.ecommerce.entity.Inventory;
import com.peddannat.ecommerce.exception.InsufficientStockException;
import com.peddannat.ecommerce.exception.ResourceNotFoundException;
import com.peddannat.ecommerce.repository.InventoryRepository;
import com.peddannat.ecommerce.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;


    @Override
    public InventoryResponse createInventory(InventoryRequest request) {


        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException("Inventory already exists for product id: " + request.getProductId());
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(request.getProductId());
        inventory.setQuantity(request.getQuantity());
        inventory.setReservedQuantity(request.getReservedQuantity());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToResponse(savedInventory);

    }

    @Override
    public InventoryResponse getInventoryByProductId(Long productId) {

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
        return mapToResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse reduceStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product id: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        return mapToResponse(inventory);
    }


    @Override
    public boolean checkStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));

        return inventory.getAvailableQuantity() >= quantity;
    }

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
