# Architecture Overview

## 목적
keeb-station 서비스의 전체 구조와 책임 분리를 설명한다.

이 문서는:
- 패키지 구조
- 계층 책임
- 도메인 설계 방향
을 한 눈에 이해하기 위한 문서다.

---

## 전체 구조

keeb-station은 Layered Architecture를 기반으로 구성한다.

- Presentation Layer (web)
- Application Layer (service)
- Domain Layer (entity)
- Infrastructure Layer (repository, external)

---

## 패키지 구조

```
com.saebom.keebstation
 ├─ domain
 │   ├─ category
 │   ├─ product
 │   ├─ option
 │   ├─ stock
 │   ├─ order
 │   ├─ payment
 │   └─ member
 ├─ web
 │   ├─ api
 │   └─ admin
 ├─ global
 │   ├─ config
 │   ├─ security
 │   ├─ exception
 │   └─ common
 └─ KeebStationApplication
```

---

## 핵심 설계 포인트

- 실제 판매 단위는 ProductOption
- 재고는 ProductOption : Stock = 1 : 1
- 주문 상세(OrderLine)는 ProductOption을 참조
- Admin 기능은 별도 도메인이 아닌 접근 주체(Role) 기준으로 분리
- 결제(Payment)는 주문(Order)과 분리된 독립 도메인
- Order는 상태 전이만 책임 (CREATED → PAID → ...)
- PaymentService를 통해서만 PAID 상태로 전이 가능

---

## 주문 생성 트랜잭션 흐름

- Controller → OrderService (@Transactional, 트랜잭션 시작)
- ProductOption 조회
- Stock 조회 및 quantity 감소
- Order / OrderLine 생성
- 총 금액 확정
- 실패 시 전체 롤백 (재고 차감 포함)

---

## 주문 결제 트랜잭션 흐름

- Controller → PaymentService (@Transactional)
- Orders 조회
- 결제 금액 검증 (order.totalPrice == amount)
- 중복 결제 방지
- Payment 생성 및 성공 처리
- Orders 상태를 PAID로 전이

---

## 비고

- 본 문서는 현재 기준 구조를 설명한다
- 구조/정책 변경 시 decisions.md에 반드시 기록한다
