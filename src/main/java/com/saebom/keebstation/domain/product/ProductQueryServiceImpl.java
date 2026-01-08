package com.saebom.keebstation.domain.product;

import com.saebom.keebstation.domain.option.ProductOption;
import com.saebom.keebstation.web.dto.api.product.ProductDetailResponse;
import com.saebom.keebstation.web.dto.api.product.ProductSummaryResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    @Override
    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = productRepository.findDetailById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. productId=" + productId));

        List<ProductDetailResponse.ProductOptionResponse> options = product.getOptions().stream()
                .map(this::toOptionResponse)
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                product.getStatus(),
                options
        );
    }

    private ProductDetailResponse.ProductOptionResponse toOptionResponse(ProductOption option) {
        return new ProductDetailResponse.ProductOptionResponse(
                option.getId(),
                option.getOptionSummary(),
                option.getExtraPrice(),
                option.getStatus(),
                option.isDefault(),
                new ProductDetailResponse.StockResponse(option.getStock().getQuantity())
        );
    }

    @Override
    public Page<ProductSummaryResponse> getProductList(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toSummaryResponse);
    }

    private ProductSummaryResponse toSummaryResponse(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getBasePrice(),
                product.getStatus()
        );
    }
}