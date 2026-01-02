package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.domain.option.ProductOption;
import com.saebom.keebstation.domain.option.ProductOptionRepository;
import com.saebom.keebstation.domain.order.dto.CreateOrderItemRequest;
import com.saebom.keebstation.domain.order.dto.CreateOrderRequest;
import com.saebom.keebstation.domain.order.dto.CreateOrderResponse;
import com.saebom.keebstation.domain.stock.Stock;
import com.saebom.keebstation.domain.stock.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderLineRepository orderLineRepository;
    private final ProductOptionRepository productOptionRepository;
    private final StockRepository stockRepository;

    public OrderServiceImpl(OrdersRepository ordersRepository,
                            OrderLineRepository orderLineRepository,
                            ProductOptionRepository productOptionRepository,
                            StockRepository stockRepository) {
        this.ordersRepository = ordersRepository;
        this.orderLineRepository = orderLineRepository;
        this.productOptionRepository = productOptionRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(Long memberId, CreateOrderRequest request) {

        if (memberId == null) throw new IllegalArgumentException("memberId는 필수입니다.");
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 비어있습니다.");
        }

        long totalPrice = 0L;
        Orders order = ordersRepository.save(new Orders(memberId, 0L));

        List<OrderLine> lines = new ArrayList<>();

        for (CreateOrderItemRequest item : request.getItems()) {
            Long optionId = item.getProductOptionId();
            int quantity = item.getQuantity();

            if (optionId == null) throw new IllegalArgumentException("productOptionId는 필수입니다.");
            if (quantity <= 0) throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");

            ProductOption productOption = productOptionRepository.findById(optionId)
                    .orElseThrow(() -> new EntityNotFoundException("옵션이 존재하지 않습니다. optionId=" + optionId));

            Stock stock = stockRepository.findByProductOptionId(optionId)
                    .orElseThrow(() -> new EntityNotFoundException("재고가 존재하지 않습니다. optionId=" + optionId));

            stock.decrease(quantity);

            long unitPrice = productOption.getProduct().getBasePrice() + productOption.getExtraPrice();
            totalPrice += unitPrice * quantity;

            lines.add(new OrderLine(order, productOption, unitPrice, quantity));
        }

        orderLineRepository.saveAll(lines);
        order.applyTotalPrice(totalPrice);

        return new CreateOrderResponse(order.getId(), totalPrice);

    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {

        if (orderId == null) throw new IllegalArgumentException("orderId는 필수입니다.");

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않습니다. orderId=" + orderId));

        order.cancel();

        List<OrderLine> lines = orderLineRepository.findByOrderId(orderId);

        for (OrderLine line : lines) {
            Long optionId = line.getProductOption().getId();
            int quantity = line.getQuantity();

            Stock stock = stockRepository.findByProductOptionId(optionId)
                    .orElseThrow(() -> new EntityNotFoundException("재고가 존재하지 않습니다. optionId=" + optionId));

            stock.increase(quantity);

        }
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않습니다. orderId=" + orderId));
        order.ship();
    }
}