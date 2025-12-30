package com.saebom.keebstation.domain.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {

    private List<CreateOrderItemRequest> items = new ArrayList<>();
}