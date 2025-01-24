package com.portfolio.controller;

import com.portfolio.model.Stock;
import com.portfolio.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/")
    public String welcome() {
        return "Welcome to the Portfolio Tracker API!";
    }

    @PostMapping
    public Stock addStock(@RequestBody Stock stock) {
        return stockService.addStock(stock);
    }

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{ticker}")
    public Stock getStock(@PathVariable String ticker) {
        return stockService.getStockByTicker(ticker);
    }

    @PutMapping("/{ticker}")
    public Stock updateStock(@PathVariable String ticker, @RequestBody Stock stock) {
        return stockService.updateStock(ticker, stock);
    }

    @DeleteMapping("/{ticker}")
    public void deleteStock(@PathVariable String ticker) {
        stockService.deleteStock(ticker);
    }

    @GetMapping("/portfolio-value")
    public double getTotalPortfolioValue() {
        return stockService.getTotalPortfolioValue();
    }
}
