package com.saebom.keebstation.domain.shipping;

import com.saebom.keebstation.domain.order.Order;
import com.saebom.keebstation.domain.order.OrderRepository;
import com.saebom.keebstation.domain.order.OrderStatus;
import com.saebom.keebstation.domain.payment.PaymentMethod;
import com.saebom.keebstation.domain.payment.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ShippingServiceTest {

    @Autowired private PaymentService paymentService;
    @Autowired private ShippingService shippingService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ShippingRepository shippingRepository;

    @Test
    void PAID_주문이면_배송_생성_성공_및_주문_SHIPPED_전이() {
        // given
        Order order = savePaidOrder(1L, 100_000L);

        // when
        shippingService.createShipping(order.getId());

        // then
        Order shipped = orderRepository.findById(order.getId()).orElseThrow();
        Shipping shipping = shippingRepository.findByOrderId(order.getId()).orElseThrow();

        assertThat(shipped.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(shipping.getStatus()).isEqualTo(ShippingStatus.READY);
    }

    @Test
    void CREATED_주문이면_배송_생성_실패() {
        // given
        Order order = orderRepository.save(Order.create(1L, 100_000L));

        // when & then
        assertThatThrownBy(() -> shippingService.createShipping(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 이미_배송이_생성된_주문은_중복_생성_불가() {
        // given
        Order order = savePaidOrder(1L, 100_000L);
        shippingService.createShipping(order.getId());

        // when & then
        assertThatThrownBy(() -> shippingService.createShipping(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void READY_배송은_SHIPPED로_전이된다() {
        // given
        Order order = savePaidOrder(1L, 100_000L);
        shippingService.createShipping(order.getId());

        // when
        shippingService.markShipped(order.getId());

        // then
        Shipping shipping = shippingRepository.findByOrderId(order.getId()).orElseThrow();
        assertThat(shipping.getStatus()).isEqualTo(ShippingStatus.SHIPPED);
    }

    @Test
    void SHIPPED_배송은_DELIVERED로_전이된다() {
        // given
        Order order = savePaidOrder(1L, 100_000L);
        shippingService.createShipping(order.getId());
        shippingService.markShipped(order.getId());

        // when
        shippingService.markDelivered(order.getId());

        // then
        Shipping shipping = shippingRepository.findByOrderId(order.getId()).orElseThrow();
        assertThat(shipping.getStatus()).isEqualTo(ShippingStatus.DELIVERED);
    }

    @Test
    void READY_배송은_바로_DELIVERED_불가() {
        // given
        Order order = savePaidOrder(1L, 100_000L);
        shippingService.createShipping(order.getId());

        // when & then
        assertThatThrownBy(() -> shippingService.markDelivered(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    private Order savePaidOrder(Long memberId, long totalPrice) {
        Order order = orderRepository.save(Order.create(memberId, totalPrice));
        paymentService.pay(order.getId(), totalPrice, PaymentMethod.CARD);
        return orderRepository.findById(order.getId()).orElseThrow();
    }
}