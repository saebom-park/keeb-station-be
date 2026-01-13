package com.saebom.keebstation.web.admin;

import com.saebom.keebstation.domain.order.OrderQueryService;
import com.saebom.keebstation.web.dto.api.order.OrderDetailResponse;
import com.saebom.keebstation.web.dto.api.order.OrderSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderQueryService orderQueryService;

    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponse>> getOrders(Pageable pageable) {
        return ResponseEntity.ok(orderQueryService.getOrderList(pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderQueryService.getOrderDetail(orderId));
    }
}