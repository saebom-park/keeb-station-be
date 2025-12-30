package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_member_id", columnList = "member_id"),
        @Index(name = "idx_orders_reg_time", columnList = "reg_time")
})
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    // v1: Member 연관관계는 나중에
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20)")
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private long totalPrice;

    public Orders(Long memberId, long totalPrice) {
        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.CREATED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void applyTotalPrice(long totalPrice) {
        if (totalPrice < 0) throw new IllegalArgumentException("totalPrice must be >= 0");
        this.totalPrice = totalPrice;
    }

}