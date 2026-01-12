# [keeb-station] ERD & Table Specifications

> keeb-station의 핵심은 **옵션 기반 판매 단위(ProductOption)** 와  
> **옵션 단위 재고(Stock 1:1)**, **주문 라인(OrderLine)이 옵션을 참조**하는 구조다.  
> 본 문서는 이 구조를 기준으로 DB 테이블/컬럼/제약을 확정한다.

---

## 0. 설계 결정 사항 (확정)

- **주문 테이블명:** `orders` (예약어/쿼리 가독성 고려)
- **금액 컬럼 타입:** 전부 `BIGINT` (원 단위 정수)
- 모든 테이블에 공통 컬럼 포함 (가정)
  - `reg_time` (TIMESTAMP) : 생성 시각
  - `update_time` (TIMESTAMP) : 수정 시각

---

## 1. 테이블 관계 요약

- `category (1) ── (N) product`
- `product (1) ── (N) product_option`
- `product_option (1) ── (1) stock` (**Unique로 1:1 강제**)
- `member (1) ── (N) orders` *(초기 단계에서는 FK만 가정)*
- `orders (1) ── (N) order_line`
- `order_line (N) ── (1) product_option`
- `orders (1) ── (N) payment`
- `orders (1) ── (1) shippings` (**주문당 배송 1건, UNIQUE(order_id)로 1:1 강제**)

---

## 2. Table Specifications

### 2.1 `category` (카테고리)

- 데이터 예시: `KEYBOARD`, `KEYCAP`, `SWITCH`, `ACCESSORY`

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| category_id | BIGINT | PK, Auto Increment | 카테고리 ID |
| name | VARCHAR(50) | NOT NULL, UNIQUE | 카테고리명 (Enum 매핑 예정) |
| reg_time | TIMESTAMP | NOT NULL | 생성 시각 |
| update_time | TIMESTAMP | NOT NULL | 수정 시각 |

---

### 2.2 `product` (상품: 개념 단위)

> “키보드 A”, “체리 스위치” 같은 **상품의 껍데기(개념)** 를 표현한다.  
> 실제로 장바구니/주문에 담기는 판매 단위는 `product_option`이다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| product_id | BIGINT | PK, Auto Increment | 상품 ID |
| category_id | BIGINT | FK (`category.category_id`) | 카테고리 ID |
| name | VARCHAR(100) | NOT NULL | 상품명 |
| description | TEXT | NOT NULL | 상세 설명(HTML 가능) |
| base_price | BIGINT | NOT NULL | 기본 가격(원 단위) |
| status | VARCHAR(20) | NOT NULL | 상태 (`ACTIVE`, `INACTIVE`) |
| reg_time | TIMESTAMP | NOT NULL | 생성 시각 |
| update_time | TIMESTAMP | NOT NULL | 수정 시각 |

권장 인덱스:
- `idx_product_category_id (category_id)`

---

### 2.3 `product_option` (상품 옵션: 실제 판매 단위) ⭐

> 실제로 “구매 가능한 단위”를 표현한다.  
> - **KEYBOARD**: 조합된 옵션 1개가 1행 (예: `Red / TKL / Black`)  
> - **KEYCAP/SWITCH/ACCESSORY**: `DEFAULT` 1개만 생성 (수량만 선택)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| product_option_id | BIGINT | PK, Auto Increment | 옵션 ID |
| product_id | BIGINT | FK (`product.product_id`) | 부모 상품 ID |
| option_summary | VARCHAR(255) | NOT NULL | 옵션명 (예: `Red / TKL / Black` 또는 `DEFAULT`) |
| extra_price | BIGINT | NOT NULL | 추가금(기본값 0) |
| status | VARCHAR(20) | NOT NULL | 상태 (`AVAILABLE`, `DISABLED`) |
| sku | VARCHAR(50) | UNIQUE | 옵션 고유 코드(어드민/재고/운영 편의) |
| is_default | BOOLEAN | NOT NULL | 단일 옵션 여부 (`TRUE`면 DEFAULT) |
| reg_time | TIMESTAMP | NOT NULL | 생성 시각 |
| update_time | TIMESTAMP | NOT NULL | 수정 시각 |

권장 규칙:
- 단일 옵션 상품(키캡/스위치/악세사리)은 `is_default = TRUE`인 옵션을 **정확히 1개**만 갖도록 운영 정책으로 강제
- KEYBOARD는 `is_default = FALSE`로 운영

권장 인덱스:
- `idx_product_option_product_id (product_id)`

---

### 2.4 `stock` (재고)

> `product_option_id` 1개당 재고 row 1개 (1:1).  
> `version`으로 낙관적 락(동시성)을 대비한다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| stock_id | BIGINT | PK, Auto Increment | 재고 ID |
| product_option_id | BIGINT | FK, UNIQUE (`product_option.product_option_id`) | 옵션 ID(1:1 강제) |
| quantity | INT | NOT NULL | 현재 수량 (0 이상) |
| version | BIGINT | NOT NULL | 낙관적 락 버전 (기본 0) |
| reg_time | TIMESTAMP | NOT NULL | 생성 시각 |
| update_time | TIMESTAMP | NOT NULL | 수정 시각 |

권장 제약:
- `quantity >= 0` (DB CHECK 또는 서비스 레이어에서 강제)
- Stock은 @Version 기반 낙관적 락으로 동시 주문 시 재고 정합성을 보장한다.
- 충돌 시 OptimisticLockException 발생을 허용하고 서비스 레벨에서 처리한다.

---

### 2.5 `orders` (주문 헤더)

> “누가, 언제, 얼마치 샀는지” 주문의 큰 정보를 담는다.  
> 초기 단계에서는 `member_id`를 FK 컬럼으로만 두고, Member 엔티티 연관관계는 뒤로 미룰 수 있다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| order_id | BIGINT | PK, Auto Increment | 주문 ID |
| member_id | BIGINT | FK (가정) | 주문자 ID |
| status | VARCHAR(20) | NOT NULL | 상태 (`CREATED`, `PAID`, `SHIPPED`, `CANCELED`) |
| total_price | BIGINT | NOT NULL | 총 주문 금액 |
| reg_time | TIMESTAMP | NOT NULL | 생성 시각 |
| update_time | TIMESTAMP | NOT NULL | 수정 시각 |

권장 인덱스:
- `idx_orders_member_id (member_id)`
- `idx_orders_reg_time (reg_time)`

---

### 2.6 `order_line` (주문 상세)

> 주문서 안의 “품목 한 줄”을 의미한다.  
> `product_option_id`를 참조하여, 주문 시점에 **정확히 어떤 옵션을 샀는지** 보존한다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| order_line_id | BIGINT | PK, Auto Increment | 주문 상세 ID |
| order_id | BIGINT | FK (`orders.order_id`) | 주문 ID |
| product_option_id | BIGINT | FK (`product_option.product_option_id`) | 주문한 옵션 상품 |
| unit_price | BIGINT | NOT NULL | 구매 당시 단가 (가격 변동 대비) |
| quantity | INT | NOT NULL | 구매 수량 |
| line_amount | BIGINT | NOT NULL | 총액 (`unit_price * quantity`) |
| reg_time | TIMESTAMP | NOT NULL | 생성 시각 |
| update_time | TIMESTAMP | NOT NULL | 수정 시각 |

권장 인덱스:
- `idx_order_line_order_id (order_id)`
- `idx_order_line_product_option_id (product_option_id)`

---

### 2.7 `payment` (결제)

> 주문에 대한 결제 이력을 관리한다.  
> 현재는 내부 시뮬레이션 단계이며, 외부 PG 연동을 고려한 확장 구조를 유지한다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| payment_id | BIGINT | PK, Auto Increment | 결제 ID |
| order_id | BIGINT | FK (`orders.order_id`) | 주문 ID |
| amount | BIGINT | NOT NULL | 결제 금액 |
| method | VARCHAR(20) | NOT NULL | 결제 수단 (`CARD`, `ACCOUNT` 등) |
| status | VARCHAR(20) | NOT NULL | 결제 상태 (`READY`, `SUCCESS`, `FAILED`) |
| reg_time | TIMESTAMP | NOT NULL | 생성 시각 |
| update_time | TIMESTAMP | NOT NULL | 수정 시각 |

권장 인덱스:
- `idx_payment_order_id (order_id)`

권장 규칙:
- 주문당 `SUCCESS` 상태의 결제는 1건만 허용
- 이미 결제 완료된 주문에 대한 추가 결제 시도는 예외 처리

---

### 2.8 `shipping` (배송)

> 주문에 대한 배송 상태를 관리한다.  
> 주문 1건당 배송 정보는 정확히 1건만 생성되며,  
> `order_id`에 UNIQUE 제약을 두어 1:1 관계를 강제한다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| shipping_id | BIGINT | PK, Auto Increment | 배송 ID |
| order_id | BIGINT | FK, UNIQUE (`orders.order_id`) | 주문 ID |
| status | VARCHAR(20) | NOT NULL | 배송 상태 (`READY`, `SHIPPED`, `DELIVERED`) |
| reg_time | TIMESTAMP | NOT NULL | 생성 시각 |
| update_time | TIMESTAMP | NOT NULL | 수정 시각 |

권장 규칙:
- 배송은 **PAID 상태의 주문에서만 생성 가능**
- 배송 생성 시 `orders.status`는 `SHIPPED`로 전이
  - SHIPPED는 발송 완료가 아니라 배송 단계 진입을 의미한다.
- 주문이 취소되면 배송은 생성될 수 없다

---

## 3. Enum 정의 (문서 기준)

- `CategoryName`: `KEYBOARD`, `KEYCAP`, `SWITCH`, `ACCESSORY`
- `ProductStatus`: `ACTIVE`, `INACTIVE`
- `ProductOptionStatus`: `AVAILABLE`, `DISABLED`
- `OrderStatus`: `CREATED`, `PAID`, `SHIPPED`, `CANCELED`
- `PaymentStatus`: `READY`, `SUCCESS`, `FAILED`
- `PaymentMethod`: `CARD`, `ACCOUNT`
- `ShippingStatus`: `READY`, `SHIPPED`, `DELIVERED`

---

## 4. 구현 가이드

- 주문 생성 시:
  1) `Stock.quantity` 감소 (옵션 기준)
  2) `orders`, `order_line` 저장
  3) 실패 시 전체 롤백 (트랜잭션)

- 주문 취소 시:
  1) `orders.status = CANCELED`
  2) `Stock.quantity` 복원

---

## 5. 변경 이력

- 2025-12-26: `orders` 테이블명 확정
- 2025-12-26: 금액 컬럼 타입을 `BIGINT`로 통일
- 2025-12-30: Stock에 `@Version` 기반 낙관적 락 적용
- 2025-12-30: 동시 주문 충돌 시 `OptimisticLockException` 발생을 허용하고 서비스 레벨에서 처리하도록 설계
- 2026-01-05: 배송(shippings) 도메인 추가 및 주문–배송 1:1 관계 확정
- 2026-01-05: 결제(payment)는 주문당 다건 이력 허용하되, 성공 결제는 1건만 허용하도록 정책 명시