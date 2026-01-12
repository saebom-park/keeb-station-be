package com.saebom.keebstation.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 플로우 통합: 주문 생성 -> 주문 상세 조회 -> 주문 취소")
    void orderFlow_create_detail_cancel() throws Exception {
        // given: 주문 생성 요청
        String createJson = """
                {
                    "memberId": 1,
                    "items": [
                        {"productOptionId": 1, "quantity": 2}
                    ]
                }
                """;

        // when: 주문 생성
        String createResponseBody = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // orderId 추출
        long orderId = objectMapper.readTree(createResponseBody).get("orderId").asLong();

        // then: 주문 상세 조회
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.status").isNotEmpty())
                .andExpect(jsonPath("$.totalPrice").isNumber())
                .andExpect(jsonPath("$.orderedAt").isNotEmpty())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()", greaterThan(0)))
                .andExpect(jsonPath("$.items[0].productOptionId").isNumber())
                .andExpect(jsonPath("$.items[0].quantity").isNumber())
                .andExpect(jsonPath("$.items[0].unitPrice").isNumber())
                .andExpect(jsonPath("$.items[0].lineAmount").isNumber());

        // when: 주문 취소
        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then: 다시 상세 조회 시 status=CANCELED 확인
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    @Test
    @DisplayName("주문 목록 조회: Page 구조와 summary 필드를 반환한다")
    void getOrders_returnsPage() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").isNumber())
                .andExpect(jsonPath("$.number").isNumber());
    }

    @Test
    @DisplayName("주문 상세 조회: 존재하지 않으면 404 NOT_FOUND를 반환한다")
    void getOrderDetail_notFound_returns404() throws Exception {
        long orderId = 999_999L;

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/orders/" + orderId));
    }
}