package com.saebom.keebstation.domain.product;

import com.saebom.keebstation.web.dto.api.product.ProductDetailResponse;
import com.saebom.keebstation.web.dto.api.product.ProductSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryService {
    ProductDetailResponse getProductDetail(Long productId);

    Page<ProductSummaryResponse> getProductList(Pageable pageable);
}