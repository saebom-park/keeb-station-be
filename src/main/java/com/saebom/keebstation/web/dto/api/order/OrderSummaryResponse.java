package com.saebom.keebstation.web.dto.api.order;

import com.saebom.keebstation.domain.order.OrderStatus;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long orderId,
        OrderStatus status,
        long totalPrice,
        LocalDateTime orderedAt
) {
}