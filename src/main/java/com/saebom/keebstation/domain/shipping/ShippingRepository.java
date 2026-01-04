package com.saebom.keebstation.domain.shipping;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    boolean existsByOrderId(Long orderId);

    Optional<Shipping> findByOrderId(Long orderId);
}