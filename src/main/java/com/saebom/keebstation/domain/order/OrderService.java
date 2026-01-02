package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.domain.order.dto.CreateOrderRequest;
import com.saebom.keebstation.domain.order.dto.CreateOrderResponse;

public interface OrderService {

    CreateOrderResponse createOrder(Long memberId, CreateOrderRequest request);
    void cancelOrder(Long orderId);
    void shipOrder(Long orderId);
}