package com.saebom.keebstation.web.dto.api.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {

    private Long memberId;

    private List<CreateOrderItemRequest> items = new ArrayList<>();
}