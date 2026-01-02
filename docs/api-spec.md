# API Specification

> keeb-station 서비스의 공개 API 명세서  
> 본 문서는 **현재 구현 완료된 기능 + 확정된 설계 기준**만을 포함한다.

---

## 0. 공통 규칙

- Base URL: `/api`
- 모든 요청/응답은 JSON 사용
- 에러 응답은 공통 `ErrorResponse` 포맷 사용

### 공통 에러 응답 형식
```json
{
  "code": "ERROR_CODE",
  "message": "에러 메시지",
  "status": 400,
  "path": "/api/..."
}
```

### 공통 HTTP Status 규칙
- `400 Bad Request`  
  - 요청 값 오류 (금액 불일치, 잘못된 상태 전이 등)
- `404 Not Found`  
  - 리소스 없음 (주문, 옵션, 재고 등)
- `409 Conflict`  
  - 비즈니스 충돌
    - 중복 결제
    - 낙관적 락 충돌(동시 주문)

---

## 1. Product API

### GET /api/products
- 설명: 상품 목록 조회
- 비고: 구현 예정

---

### GET /api/products/{id}
- 설명: 상품 상세 조회
- 비고: 구현 예정

---

## 2. Order API

### POST /api/orders?memberId={memberId}
- 설명: 주문 생성 (옵션 기준)

#### Request
```json
{
  "items": [
    {
      "productOptionId": 1,
      "quantity": 1
    }
  ]
}
```

#### 처리 흐름
1. ProductOption 조회
2. Stock 조회 및 수량 감소 (@Version 기반 낙관적 락)
3. Orders / OrderLine 생성
4. 총 금액 계산 및 확정
5. 실패 시 전체 롤백

#### Response
```json
{
  "orderId": 1,
  "totalPrice": 100000
}
```

#### Error Case
- `404 Not Found` : 옵션/재고 없음
- `409 Conflict` : 재고 동시성 충돌

---

### POST /api/orders/{orderId}/cancel
- 설명: 주문 취소

#### 제약 조건
- `CREATED` 상태에서만 취소 가능
- `PAID` 상태에서는 취소 불가

#### 처리 결과
- 주문 상태 → `CANCELED`
- 재고 수량 복원

#### Response
- `200 OK`

#### Error Case
- `404 Not Found` : 주문 없음
- `400 Bad Request` : 취소 불가능한 상태

---

## 3. Payment API

> 결제는 Order 도메인과 분리된 **Payment 도메인**에서 처리한다.  
> 현재는 내부 시뮬레이션 단계이며, 외부 PG 연동을 고려한 구조를 유지한다.

---

### POST /api/orders/{orderId}/payments
- 설명: 주문에 대한 결제 생성

#### Request
```json
{
  "amount": 100000,
  "method": "CARD"
}
```

#### 처리 흐름
1. Orders 조회
2. 이미 성공한 결제 존재 여부 확인
3. 결제 금액 == 주문 총액 검증
4. Payment 생성
5. Payment 상태 → `SUCCESS`
6. Orders 상태 → `PAID`

#### Response
- `200 OK`

#### Error Case
- `404 Not Found`
  - 주문 없음
- `400 Bad Request`
  - 결제 금액 불일치
  - 잘못된 요청 값
- `409 Conflict`
  - 이미 결제된 주문

---

## 4. 주문 상태 요약

| 상태 | 설명 |
|---|---|
| CREATED | 주문 생성 완료 |
| PAID | 결제 완료 |
| CANCELED | 주문 취소 |

> 배송(Shipping) 관련 상태는 **다음 단계에서 확장 예정**

---

## 5. 비고

- 결제 API는 `/pay` command-style 대신  
  **리소스 기반 `/payments` 경로**를 사용한다.
- 주문 상태 변경은 각 도메인의 책임에 따라 분리된다.
  - Order: 상태 전이 규칙
  - Payment: 결제 및 PAID 전이
