package com.saebom.keebstation.web.api;

import com.saebom.keebstation.domain.order.OrderQueryService;
import com.saebom.keebstation.domain.order.OrderService;
import com.saebom.keebstation.web.dto.api.order.CreateOrderRequest;
import com.saebom.keebstation.web.dto.api.order.CreateOrderResponse;
import com.saebom.keebstation.web.dto.api.order.OrderDetailResponse;
import com.saebom.keebstation.web.dto.api.order.OrderSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;

    @PostMapping()
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Page<OrderSummaryResponse> getOrders(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return orderQueryService.getOrderList(pageable);
    }

    @GetMapping("/{orderId}")
    public OrderDetailResponse getOrderDetail(@PathVariable Long orderId) {
        return orderQueryService.getOrderDetail(orderId);
    }
}