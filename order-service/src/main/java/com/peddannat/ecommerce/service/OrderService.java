package com.peddannat.ecommerce.service;

import com.peddannat.ecommerce.dto.request.OrderRequest;
import com.peddannat.ecommerce.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest request);

    OrderResponse getOrderById(Long orderId);

    List<OrderResponse> getOrdersByUserId(Long userId);

    OrderResponse cancelOrder(Long orderId);

    List<OrderResponse> getAllOrders();

}
