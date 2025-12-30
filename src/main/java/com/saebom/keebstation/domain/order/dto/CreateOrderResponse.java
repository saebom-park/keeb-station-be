package com.saebom.keebstation.domain.order.dto;

import lombok.Getter;

@Getter
public class CreateOrderResponse {

    private final Long orderId;
    private final long totalPrice;

    public CreateOrderResponse(Long orderId, long totalPrice) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

}