package com.saebom.keebstation.domain.shipping;

import com.saebom.keebstation.domain.order.Order;
import com.saebom.keebstation.domain.order.OrderRepository;
import com.saebom.keebstation.domain.order.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShippingServiceImpl implements ShippingService {

    private final OrderRepository orderRepository;
    private final ShippingRepository shippingRepository;

    @Override
    public void createShipping(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("PAID 상태에서만 배송을 생성할 수 있습니다.");
        }

        if (shippingRepository.existsByOrderId(orderId)) {
            throw new IllegalStateException("이미 배송이 생성된 주문입니다.");
        }

        Shipping shipping = Shipping.ready(order);
        shippingRepository.save(shipping);

        // 주문 상태 전이
        order.startShipping();
    }

    @Override
    public void markShipped(Long orderId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("배송 정보를 찾을 수 없습니다."));

        shipping.markShipped();
    }

    @Override
    public void markDelivered(Long orderId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("배송 정보를 찾을 수 없습니다."));

        shipping.markDelivered();
    }
}