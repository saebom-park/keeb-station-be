package com.saebom.keebstation.domain.shipping;

import com.saebom.keebstation.domain.order.Order;
import com.saebom.keebstation.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "shipping", uniqueConstraints = {
        @UniqueConstraint(name = "uk_shipping_order_id", columnNames = "order_id")
})
public class Shipping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_shipping_order")
    )
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20)")
    private ShippingStatus status;

    private Shipping(Order order) {
        this.order = order;
        this.status = ShippingStatus.READY;
    }

    public static Shipping ready(Order order) {
        return new Shipping(order);
    }

    // 상태 전이 검증 추가
    public void markShipped() {
        if (this.status != ShippingStatus.READY) {
            throw new IllegalStateException("READY 상태에서만 발송 처리할 수 있습니다.");
        }
        this.status = ShippingStatus.SHIPPED;
    }

    public void markDelivered() {
        if (this.status != ShippingStatus.SHIPPED) {
            throw new IllegalStateException("SHIPPED 상태에서만 배송 완료 처리할 수 있습니다.");
        }
        this.status = ShippingStatus.DELIVERED;
    }
}