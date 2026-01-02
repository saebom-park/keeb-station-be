package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.domain.option.ProductOption;
import com.saebom.keebstation.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "order_line", indexes = {
        @Index(name = "idx_order_line_order_id", columnList = "order_id"),
        @Index(name = "idx_order_line_product_option_id", columnList = "product_option_id")
})
public class OrderLine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_line_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @Column(name = "unit_price", nullable = false)
    private long unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "line_amount", nullable = false)
    private long lineAmount;

    public OrderLine(Order order, ProductOption productOption, long unitPrice, int quantity) {
        this.order = order;
        this.productOption = productOption;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineAmount = unitPrice * quantity;
    }

}