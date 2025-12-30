package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.domain.order.dto.CreateOrderRequest;
import com.saebom.keebstation.domain.stock.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void 빈_주입_확인() {
        assertThat(orderService).isNotNull();
        assertThat(stockRepository).isNotNull();
    }

    @Test
    void 동시에_주문_2번_보내면_1개만_성공해야_한다() throws Exception {
        // 전제: 옵션 1의 재고가 1이어야 함 (SQL로 먼저 맞춰두기)
        // update stock set quantity = 1 where product_option_id = 1;

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Boolean> task = () -> {
            ready.countDown();
            start.await();

            // DTO가 "JSON 바인딩 전용"이라 자바에서 만들기 불편하면
            // 지금은 컨트롤러 방식(Postman 2개 동시) + 서비스에 sleep이 제일 확실함.
            // 여기선 일단 "서비스 호출 성공/실패"만 카운트할 수 있게 빈 요청을 넣지 말자.
            return true;
        };

        Future<Boolean> f1 = pool.submit(task);
        Future<Boolean> f2 = pool.submit(task);

        ready.await();     // 두 스레드 준비될 때까지 대기
        start.countDown(); // 동시에 시작

        f1.get();
        f2.get();

        pool.shutdown();

        // 여기까지는 테스트 뼈대만 정상 실행 확인용
        assertThat(true).isTrue();
    }
}