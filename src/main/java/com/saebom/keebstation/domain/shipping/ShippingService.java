package com.saebom.keebstation.domain.shipping;

public interface ShippingService {

    void createShipping(Long orderId);

    void markShipped(Long orderId);

    void markDelivered(Long orderId);
}