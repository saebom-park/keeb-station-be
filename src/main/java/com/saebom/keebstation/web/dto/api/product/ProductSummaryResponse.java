package com.saebom.keebstation.web.dto.api.product;

import com.saebom.keebstation.domain.product.ProductStatus;

public record ProductSummaryResponse(
        Long productId,
        Long categoryId,
        String name,
        long basePrice,
        ProductStatus status
) {
}