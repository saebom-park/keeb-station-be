package com.saebom.keebstation.web;

import com.saebom.keebstation.domain.shipping.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders/{orderId}/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping
    public ResponseEntity<Void> createShipping(@PathVariable Long orderId) {
        shippingService.createShipping(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ship")
    public ResponseEntity<Void> markShipped(@PathVariable Long orderId) {
        shippingService.markShipped(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deliver")
    public ResponseEntity<Void> markDelivered(@PathVariable Long orderId) {
        shippingService.markDelivered(orderId);
        return ResponseEntity.ok().build();
    }
}