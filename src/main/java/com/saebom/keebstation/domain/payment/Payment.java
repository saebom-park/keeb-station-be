package com.saebom.keebstation.domain.payment;

import com.saebom.keebstation.domain.order.Order;
import com.saebom.keebstation.global.common.jpa.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "payment", indexes = {
        @Index(name = "idx_payment_order_id", columnList = "order_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_reg_time", columnList = "reg_time")
})
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20)")
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, columnDefinition = "varchar(20)")
    private PaymentMethod method;

    public Payment(Order order, long amount, PaymentMethod method) {
        if (order == null) throw new IllegalArgumentException("order는 필수입니다.");
        if (amount <= 0) throw new IllegalArgumentException("amount는 1 이상이어야 합니다.");
        if (method == null) throw new IllegalArgumentException("method는 필수입니다.");

        this.order = order;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.READY;
    }

    public void success() {
        if (this.status != PaymentStatus.READY) {
            throw new IllegalStateException("READY 상태에서만 결제 성공 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.SUCCESS;
    }

    public void fail() {
        if (this.status != PaymentStatus.READY) {
            throw new IllegalStateException("READY 상태에서만 결제 실패 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.FAILED;
    }
}