package com.saebom.keebstation.domain.option;

public interface ProductOptionService {

    Long createOptionWithStock(
            Long productId,
            String optionSummary,
            long extraPrice,
            ProductOptionStatus status,
            String sku,
            boolean isDefault,
            int initialQuantity
    );
}