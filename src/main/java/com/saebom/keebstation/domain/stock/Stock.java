package com.saebom.keebstation.domain.stock;

import com.saebom.keebstation.domain.option.ProductOption;
import com.saebom.keebstation.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "stock",
        uniqueConstraints = @UniqueConstraint(name = "uk_stock_product_option_id", columnNames = "product_option_id"))
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_option_id", nullable = false, unique = true)
    private ProductOption productOption;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    private Stock(ProductOption productOption, int quantity) {
        linkProductOption(productOption);
        this.quantity = quantity;
    }

    public static Stock create(ProductOption productOption, int quantity) {
        return new Stock(productOption, quantity);
    }

    private void linkProductOption(ProductOption productOption) {
        this.productOption = productOption;
        productOption.attachStock(this);
    }

    public void decrease(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (this.quantity < amount) throw new IllegalStateException("재고 부족");
        this.quantity -= amount;
    }

    public void increase(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        this.quantity += amount;
    }

}