package com.saebom.keebstation.domain.order;

import com.saebom.keebstation.web.dto.api.order.OrderDetailResponse;
import com.saebom.keebstation.web.dto.api.order.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderQueryService {

    Page<OrderSummaryResponse> getOrderList(Pageable pageable);

    OrderDetailResponse getOrderDetail(Long orderId);
}