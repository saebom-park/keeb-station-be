package com.saebom.keebstation.web;

import com.saebom.keebstation.domain.order.OrderService;
import com.saebom.keebstation.domain.order.dto.CreateOrderRequest;
import com.saebom.keebstation.domain.order.dto.CreateOrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestParam Long memberId,
            @RequestBody CreateOrderRequest request
    ) {
        CreateOrderResponse response = orderService.createOrder(memberId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<Void> ship(@PathVariable("orderId") Long orderId) {
        orderService.shipOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}