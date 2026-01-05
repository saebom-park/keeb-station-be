package com.saebom.keebstation.web.dto.api.product;

import com.saebom.keebstation.domain.option.ProductOptionStatus;
import com.saebom.keebstation.domain.product.ProductStatus;

import java.util.List;

public record ProductDetailResponse(
        Long productId,
        Long categoryId,
        String name,
        String description,
        long basePrice,
        ProductStatus status,
        List<ProductOptionResponse> options
) {
    public record ProductOptionResponse(
            Long productOptionId,
            String optionSummary,
            long extraPrice,
            ProductOptionStatus status,
            boolean isDefault,
            StockResponse stock
    ) {}

    public record StockResponse(
            int quantity
    ) {}
}