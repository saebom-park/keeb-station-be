package com.saebom.keebstation.domain.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductOptionId(Long productOptionId);
    boolean existsByProductOptionId(Long productOptionId);
}