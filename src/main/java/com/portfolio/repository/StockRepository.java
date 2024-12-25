package com.portfolio.repository;

import com.portfolio.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
    // Custom query methods can be added here if needed
}
