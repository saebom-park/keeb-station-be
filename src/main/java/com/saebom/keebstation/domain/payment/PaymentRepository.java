package com.saebom.keebstation.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);

    Optional<Payment> findTopByOrderIdAndStatusOrderByRegTimeDesc(
            Long orderId,
            PaymentStatus status
    );
}