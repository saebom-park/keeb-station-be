package com.saebom.keebstation.domain.option;

import com.saebom.keebstation.domain.product.Product;
import com.saebom.keebstation.domain.product.ProductRepository;
import com.saebom.keebstation.domain.stock.Stock;
import com.saebom.keebstation.domain.stock.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductOptionServiceImpl implements ProductOptionService {

    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    public Long createOptionWithStock(
            Long productId,
            String optionSummary,
            long extraPrice,
            ProductOptionStatus status,
            String sku,
            boolean isDefault,
            int initialQuantity
    ) {
        if (initialQuantity < 0) throw new IllegalArgumentException("initialQuantity must be >= 0");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다. productId=" + productId));

        ProductOption option = ProductOption.create(
                product, optionSummary, extraPrice, status, sku, isDefault
        );
        productOptionRepository.save(option);

        Stock stock = Stock.create(option, initialQuantity);
        stockRepository.save(stock);

        return option.getId();
    }
}