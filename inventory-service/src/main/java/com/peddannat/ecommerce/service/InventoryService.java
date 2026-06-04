package com.peddannat.ecommerce.service;

import com.peddannat.ecommerce.dto.request.InventoryRequest;
import com.peddannat.ecommerce.dto.response.InventoryResponse;

public interface InventoryService {

    InventoryResponse createInventory(InventoryRequest request);

    InventoryResponse getInventoryByProductId(Long productId);

    InventoryResponse addStock(Long productId, int quantity);

    InventoryResponse reduceStock(Long productId, int quantity);

    boolean checkStock(Long productId, int quantity);
}
