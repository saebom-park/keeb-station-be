package com.saebom.keebstation.domain.option;

import com.saebom.keebstation.domain.product.Product;
import com.saebom.keebstation.domain.product.ProductRepository;
import com.saebom.keebstation.domain.stock.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductOptionServiceTest {

    @Autowired private ProductOptionService productOptionService;
    @Autowired private ProductOptionRepository productOptionRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StockRepository stockRepository;

    @Test
    void 옵션_생성시_재고가_반드시_함께_생성된다() {
        // given
        Product product = productRepository.findById(1L).orElseThrow();

        // when
        Long optionId = productOptionService.createOptionWithStock(
                product.getId(),
                "black",
                0L,
                ProductOptionStatus.AVAILABLE,
                "SKU-001",
                true,
                10
        );

        // then
        ProductOption option = productOptionRepository.findById(optionId).orElseThrow();
        assertThat(option.getStock()).isNotNull(); // 트랜잭션이라 안정적

        assertThat(stockRepository.existsByProductOptionId(optionId)).isTrue(); // 정책을 DB로도 확인
    }
}