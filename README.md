# ⌨️ keeb-station-be

본 리포는 **keeb-station 서비스의 Backend API 서버**를 다룬다.

키보드 취미 기반 e-commerce 서비스를 가정하여  
상품 옵션·재고·주문을 중심으로 **도메인 모델링, 트랜잭션 경계, 동시성 제어, 인증·인가**를 설계·구현한다.

---

## 💡 서비스 개요

keeb-station은 **기계식 키보드 및 커스텀 파츠를 판매하는 쇼핑몰 서비스**를 가정한다.

- 상품은 옵션 단위(ProductOption)로 판매된다.
- 옵션 단위로 재고를 관리한다.
- 주문은 재고 차감 → 결제 → 상태 전이 흐름을 가진다.
- 인증은 JWT 기반이며, 사용자와 관리자의 권한을 분리한다.

---

## 📋 기능 범위

### 1. 회원 (Member)
- 이메일 기반 회원가입 및 로그인
- 비밀번호 암호화 저장
- JWT 기반 인증
- ROLE_USER / ROLE_ADMIN 권한 분리

### 2. 상품 / 옵션 / 재고
- 상품 등록 및 조회
- 옵션 단위 판매 구조
- 옵션 단위 재고 관리
- 재고 부족 시 주문 불가

### 3. 주문 (Order)
- 주문 생성 (옵션 기준)
- 주문 상태 관리 (CREATED, PAID, SHIPPED, CANCELED)
- 주문 생성 시 재고 차감
- 주문 취소 시 재고 복원
- 트랜잭션 기반 처리

### 4. 결제 / 배송
- 결제(Payment) 도메인 분리
- 주문당 성공 결제 1건 정책
- 배송(Shipping) 도메인 분리 및 주문 1:1 관리

---

## 🎯 설계 기준

- 실제 판매 단위는 **ProductOption**
- Stock은 ProductOption과 **1:1 관계**
- 재고 동시성은 `@Version` 기반 낙관적 락으로 제어
- 주문 / 결제 / 배송은 도메인 책임에 따라 분리
- 상태 전이는 서비스 계층에서 명시적으로 제어

---

## 📂 패키지 구조

```
com.saebom.keebstation
├─ domain
│ ├─ category
│ ├─ product
│ ├─ option
│ ├─ stock
│ ├─ order
│ ├─ payment
│ ├─ shipping
│ └─ member
├─ web
├─ global
│ ├─ config
│ ├─ security
│ ├─ exception
│ └─ common
└─ KeebStationApplication
```

---

## 🛠 Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Security (JWT)
- MySQL
- Gradle