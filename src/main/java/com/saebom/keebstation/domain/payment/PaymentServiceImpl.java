package com.saebom.keebstation.domain.payment;

import com.saebom.keebstation.domain.order.Order;
import com.saebom.keebstation.domain.order.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void pay(Long orderId, long amount, PaymentMethod method) {

        if (orderId == null) throw new IllegalArgumentException("orderId는 필수입니다.");
        if (amount <= 0) throw new IllegalArgumentException("amount는 1 이상이어야 합니다.");
        if (method == null) throw new IllegalArgumentException("paymentMethod는 필수입니다.");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않습니다. orderId=" + orderId));

        // 이미 성공한 결제가 있으면 차단
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.SUCCESS)) {
            throw new IllegalStateException("이미 결제가 완료된 주문입니다.");
        }

        // 결제 금액 검증
        if (order.getTotalPrice() != amount) {
            throw new IllegalArgumentException("결제 금액이 주문 금액과 일치하지 않습니다.");
        }

        Payment payment = Payment.ready(order, amount, method);

        // 내부 시뮬레이션: 지금은 무조건 성공
        payment.success();
        paymentRepository.save(payment);
        order.markPaid();
    }
}