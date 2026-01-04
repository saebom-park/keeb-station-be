package com.saebom.keebstation.domain.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderStatusTransitionTest {

    @Test
    void CREATED에서_PAID로_전이할수있다() {
        // given
        Order order = Order.create(1L, 0L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        // when
        order.markPaid();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void PAID에서_SHIPPED로_전이할수있다() {
        // given
        Order order = Order.create(1L, 0L);
        order.markPaid();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        // when
        order.startShipping();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void CREATED에서_SHIPPED로_바로_전이하면_예외가_발생한다() {
        // given
        Order order = Order.create(1L, 0L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        // when / then
        assertThatThrownBy(order::startShipping)
                .isInstanceOf(IllegalStateException.class);
    }
}