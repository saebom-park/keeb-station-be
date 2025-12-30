package com.saebom.keebstation.domain.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOrderItemRequest {

    private Long productOptionId;
    private int quantity;
}