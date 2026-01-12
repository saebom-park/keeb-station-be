package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.domain.payment.Payment;
import com.saebom.keebstation.domain.payment.PaymentRepository;
import com.saebom.keebstation.domain.payment.PaymentStatus;
import com.saebom.keebstation.domain.shipping.Shipping;
import com.saebom.keebstation.domain.shipping.ShippingRepository;
import com.saebom.keebstation.web.dto.api.order.OrderDetailResponse;
import com.saebom.keebstation.web.dto.api.order.OrderSummaryResponse;
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
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;

    @Override
    public Page<OrderSummaryResponse> getOrderList(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getStatus(),
                        order.getTotalPrice(),
                        order.getRegTime()
                ));
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. orderId=" + orderId));

        List<OrderLine> orderLines = orderLineRepository.findByOrderId(orderId);

        OrderDetailResponse.PaymentInfo paymentInfo = paymentRepository
                .findTopByOrderIdAndStatusOrderByRegTimeDesc(orderId, PaymentStatus.SUCCESS)
                .map(this::toPaymentInfo)
                .orElse(null);

        OrderDetailResponse.ShippingInfo shippingInfo = shippingRepository
                .findByOrderId(orderId)
                .map(this::toShippingInfo)
                .orElse(null);

        List<OrderDetailResponse.OrderLineResponse> items = orderLines.stream()
                .map(this::toOrderLineResponse)
                .toList();

        return new OrderDetailResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getRegTime(),
                paymentInfo,
                shippingInfo,
                items
        );
    }

    private OrderDetailResponse.OrderLineResponse toOrderLineResponse(OrderLine line) {
        return new OrderDetailResponse.OrderLineResponse(
                line.getId(),
                line.getProductOption().getId(),
                line.getProductOption().getOptionSummary(),
                line.getUnitPrice(),
                line.getQuantity(),
                line.getLineAmount()
        );
    }

    private OrderDetailResponse.PaymentInfo toPaymentInfo(Payment payment) {
        return new OrderDetailResponse.PaymentInfo(
                payment.getId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getRegTime()
        );
    }

    private OrderDetailResponse.ShippingInfo toShippingInfo(Shipping shipping) {
        return new OrderDetailResponse.ShippingInfo(
                shipping.getId(),
                shipping.getStatus()
        );
    }
}