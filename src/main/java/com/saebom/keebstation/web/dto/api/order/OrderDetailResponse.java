package com.saebom.keebstation.web.dto.api.order;

import com.saebom.keebstation.domain.order.OrderStatus;
import com.saebom.keebstation.domain.payment.PaymentStatus;
import com.saebom.keebstation.domain.shipping.ShippingStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long orderId,
        OrderStatus status,
        long totalPrice,
        LocalDateTime orderedAt,
        PaymentInfo payment,
        ShippingInfo shipping,
        List<OrderLineResponse> items
) {

    public record PaymentInfo(
            Long paymentId,
            PaymentStatus status,
            long paidAmount,
            LocalDateTime paidAt
    ) {}

    public record ShippingInfo(
            Long shippingId,
            ShippingStatus status
    ) {}

    public record OrderLineResponse(
            Long orderLineId,
            Long productOptionId,
            String optionSummary,
            long unitPrice,
            int quantity,
            long lineAmount
    ) {}
}