package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.domain.option.ProductOption;
import com.saebom.keebstation.domain.option.ProductOptionRepository;
import com.saebom.keebstation.domain.payment.PaymentMethod;
import com.saebom.keebstation.domain.payment.PaymentService;
import com.saebom.keebstation.domain.stock.Stock;
import com.saebom.keebstation.domain.stock.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderCancelStockRestoreTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineRepository orderLineRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void 주문을_취소하면_주문라인_기준으로_재고가_복원된다() {
        // given
        Long memberId = 1L;

        // 전제: DB에 product_option_id=1 이 존재해야 함
        Long optionId = 1L;

        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("옵션이 존재하지 않습니다. optionId=" + optionId));

        Stock stock = stockRepository.findByProductOptionId(optionId)
                .orElseThrow(() -> new EntityNotFoundException("재고가 존재하지 않습니다. optionId=" + optionId));

        int beforeQty = stock.getQuantity();

        Order order = orderRepository.save(new Order(memberId, 0L));
        orderLineRepository.save(new OrderLine(order, option, 1000L, 2)); // quantity=2

        // when
        orderService.cancelOrder(order.getId());

        // then
        Stock after = stockRepository.findByProductOptionId(optionId)
                .orElseThrow();

        assertThat(after.getQuantity()).isEqualTo(beforeQty + 2);

        Order reloaded = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void PAID_상태에서는_취소할수없고_재고도_변하면안된다() {
        // given
        Long memberId = 1L;
        Long optionId = 1L;

        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("옵션이 존재하지 않습니다. optionId=" + optionId));

        Stock stock = stockRepository.findByProductOptionId(optionId)
                .orElseThrow(() -> new EntityNotFoundException("재고가 존재하지 않습니다. optionId=" + optionId));

        int beforeQty = stock.getQuantity();

        Order order = orderRepository.save(new Order(memberId, 2000L));
        orderLineRepository.save(new OrderLine(order, option, 1000L, 2));

        // 총액 확정
        order.applyTotalPrice(2000L);

        // 주문을 결제 상태로 전이 (Payment 도메인 사용)
        paymentService.pay(order.getId(), 2000L, PaymentMethod.CARD);

        // when / then
        assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                .isInstanceOf(IllegalStateException.class);

        Stock after = stockRepository.findByProductOptionId(optionId).orElseThrow();
        assertThat(after.getQuantity()).isEqualTo(beforeQty); // 취소 실패면 재고도 그대로
    }
}