package com.saebom.keebstation.domain.option;

import com.saebom.keebstation.domain.product.Product;
import com.saebom.keebstation.domain.stock.Stock;
import com.saebom.keebstation.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product_option", indexes = {
        @Index(name = "idx_product_option_product_id", columnList = "product_id")
})
public class ProductOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "option_summary", nullable = false, length = 255)
    private String optionSummary;

    @Column(name = "extra_price", nullable = false)
    private long extraPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20)")
    private ProductOptionStatus status;

    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @OneToOne(mappedBy = "productOption", fetch = FetchType.LAZY)
    private Stock stock;

    private ProductOption(Product product, String optionSummary, long extraPrice,
                         ProductOptionStatus status, String sku, boolean isDefault) {
        this.product = product;
        this.optionSummary = optionSummary;
        this.extraPrice = extraPrice;
        this.status = status;
        this.sku = sku;
        this.isDefault = isDefault;
    }

    public static ProductOption create(Product product, String optionSummary, long extraPrice,
                                       ProductOptionStatus status, String sku, boolean isDefault) {
        return new ProductOption(product, optionSummary, extraPrice, status, sku, isDefault);
    }

    // 연관관계 편의 메서드
    public void attachStock(Stock stock) {
        if (stock == null) throw new IllegalArgumentException("stock must not be null");
        if (stock.getProductOption() != this) {
            throw new IllegalStateException("stock.productOption must be this");
        }
        this.stock = stock;
    }
}