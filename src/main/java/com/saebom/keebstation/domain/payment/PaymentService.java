package com.saebom.keebstation.domain.payment;

public interface PaymentService {

    void pay(Long orderId, long amount, PaymentMethod method);
}