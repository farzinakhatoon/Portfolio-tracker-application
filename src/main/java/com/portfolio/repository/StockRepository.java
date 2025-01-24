package com.portfolio.repository;

import com.portfolio.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, String> {
    List<Stock> findByUserId(Long userId);
}
