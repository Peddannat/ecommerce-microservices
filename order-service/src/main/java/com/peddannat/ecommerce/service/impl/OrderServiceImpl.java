package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.dto.request.OrderItemRequest;
import com.peddannat.ecommerce.dto.request.OrderRequest;
import com.peddannat.ecommerce.dto.response.ApiResponse;
import com.peddannat.ecommerce.dto.response.OrderItemResponse;
import com.peddannat.ecommerce.dto.response.OrderResponse;
import com.peddannat.ecommerce.dto.response.ProductResponse;
import com.peddannat.ecommerce.entity.Order;
import com.peddannat.ecommerce.entity.OrderItem;
import com.peddannat.ecommerce.entity.OrderStatus;
import com.peddannat.ecommerce.exception.InsufficientStockException;
import com.peddannat.ecommerce.exception.ResourceNotFoundException;
import com.peddannat.ecommerce.external.InventoryClient;
import com.peddannat.ecommerce.external.ProductClient;
import com.peddannat.ecommerce.repository.OrderRepository;
import com.peddannat.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Order service implementation containing business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        log.info("Placing order for userId={}", request.getUserId());

        // Step 1: Validate all items have sufficient stock before touching anything
        validateStock(request.getItems());

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            // Step 2: Fetch and validate product from product-service
            ProductResponse product = fetchActiveProduct(itemRequest.getProductId());

            if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Invalid product price for product id: " + itemRequest.getProductId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());

            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
            );

            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // Step 3: Save order as PENDING first
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved as PENDING. orderId={}", savedOrder.getId());

        // Step 4: Reduce stock for each item
        reduceInventory(savedOrder.getItems());

        // Step 5: Confirm order after successful stock reduction
        savedOrder.setStatus(OrderStatus.CONFIRMED);
        Order confirmedOrder = orderRepository.save(savedOrder);

        log.info("Order confirmed. orderId={}", confirmedOrder.getId());

        return mapToOrderResponse(confirmedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        log.info("Fetching order by id={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {

        log.info("Fetching orders for userId={}", userId);

        return orderRepository.findByUserIdOrderByOrderDateDesc(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        log.info("Cancelling order id={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered order cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Shipped order cannot be cancelled");
        }

        // Only restore inventory if stock was already reduced
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            restoreInventory(order.getItems());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        log.info("Order cancelled. orderId={}", orderId);
        return mapToOrderResponse(cancelledOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");

        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    /**
     * Validates that sufficient stock exists for all items before placing the order.
     */
    private void validateStock(List<OrderItemRequest> items) {
        for (OrderItemRequest itemRequest : items) {
            ApiResponse<Boolean> stockResponse = inventoryClient.checkStock(
                    itemRequest.getProductId(),
                    itemRequest.getQuantity()
            );

            if (stockResponse == null || !stockResponse.isSuccess() || !Boolean.TRUE.equals(stockResponse.getData())) {
                throw new InsufficientStockException(
                        "Insufficient stock for product id: " + itemRequest.getProductId()
                );
            }
        }
    }

    /**
     * Fetches product from product-service and validates it is active.
     */
    private ProductResponse fetchActiveProduct(Long productId) {
        ApiResponse<ProductResponse> productResponse = productClient.getProductById(productId);

        if (productResponse == null || !productResponse.isSuccess() || productResponse.getData() == null) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        ProductResponse product = productResponse.getData();

        if (!product.isActive()) {
            throw new IllegalStateException("Product is inactive with id: " + productId);
        }

        return product;
    }

    /**
     * Reduces stock in inventory-service for all order items.
     */
    private void reduceInventory(List<OrderItem> items) {
        for (OrderItem item : items) {
            log.info("Reducing stock. productId={}, quantity={}", item.getProductId(), item.getQuantity());

            ApiResponse<?> reduceResponse = inventoryClient.reduceStock(item.getProductId(), item.getQuantity());

            if (reduceResponse == null || !reduceResponse.isSuccess()) {
                throw new InsufficientStockException(
                        "Failed to reduce stock for product id: " + item.getProductId()
                );
            }
        }
    }

    /**
     * Restores stock in inventory-service when an order is cancelled.
     */
    private void restoreInventory(List<OrderItem> items) {
        for (OrderItem item : items) {
            log.info("Restoring stock. productId={}, quantity={}", item.getProductId(), item.getQuantity());

            ApiResponse<?> addStockResponse = inventoryClient.addStock(item.getProductId(), item.getQuantity());

            if (addStockResponse == null || !addStockResponse.isSuccess()) {
                throw new IllegalStateException(
                        "Failed to restore stock for product id: " + item.getProductId()
                );
            }
        }
    }

    /**
     * Converts Order entity to OrderResponse DTO.
     */
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderDate(),
                itemResponses
        );
    }
}