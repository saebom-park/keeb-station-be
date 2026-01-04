package com.saebom.keebstation.domain.payment;

import com.saebom.keebstation.domain.order.OrderStatus;
import com.saebom.keebstation.domain.order.Order;
import com.saebom.keebstation.domain.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PaymentServiceTest {

    @Autowired private PaymentService paymentService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Test
    void 정상_결제_성공() {
        // given
        Order order = orderRepository.save(Order.create(1L, 100_000L));

        // when
        paymentService.pay(order.getId(), 100_000L, PaymentMethod.CARD);

        // then
        Order paid = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(paid.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)).isTrue();
    }

    @Test
    void 결제_금액이_다르면_실패() {
        Order order = orderRepository.save(Order.create(1L, 100_000L));

        assertThatThrownBy(() ->
                paymentService.pay(order.getId(), 90_000L, PaymentMethod.CARD)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이미_결제된_주문은_다시_결제_불가() {
        Order order = orderRepository.save(Order.create(1L, 100_000L));
        paymentService.pay(order.getId(), 100_000L, PaymentMethod.CARD);

        assertThatThrownBy(() ->
                paymentService.pay(order.getId(), 100_000L, PaymentMethod.CARD)
        ).isInstanceOf(IllegalStateException.class);
    }
}