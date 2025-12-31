package com.saebom.keebstation.domain.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrdersStatusTransitionTest {

    @Test
    void CREATED에서_PAID로_전이할수있다() {
        // given
        Orders order = new Orders(1L, 0L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        // when
        order.markPaid();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void PAID에서_SHIPPED로_전이할수있다() {
        // given
        Orders order = new Orders(1L, 0L);
        order.markPaid();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        // when
        order.ship();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void CREATED에서_SHIPPED로_바로_전이하면_예외가_발생한다() {
        // given
        Orders order = new Orders(1L, 0L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        // when / then
        assertThatThrownBy(order::ship)
                .isInstanceOf(IllegalStateException.class);
    }
}