package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_member_id", columnList = "member_id"),
        @Index(name = "idx_orders_reg_time", columnList = "reg_time")
})
public class Order extends BaseTimeEntity {

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

    private Order(Long memberId, long totalPrice) {
        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.CREATED;
    }

    public static Order create(Long memberId, long totalPrice) {
        return new Order(memberId, totalPrice);
    }

    public void applyTotalPrice(long totalPrice) {
        if (totalPrice < 0) throw new IllegalArgumentException("totalPrice must be >= 0");
        this.totalPrice = totalPrice;
    }

    // 상태 전이 검증 추가
    public void cancel() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("CREATED 상태에서만 취소할 수 있습니다.");
        }
        this.status = OrderStatus.CANCELED;
    }

    public void markPaid() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("CREATED 상태에서만 결제 완료 처리할 수 있습니다.");
        }
        this.status = OrderStatus.PAID;
    }

    public void startShipping() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("PAID 상태에서만 배송 처리할 수 있습니다.");
        }
        this.status = OrderStatus.SHIPPED;
    }

}