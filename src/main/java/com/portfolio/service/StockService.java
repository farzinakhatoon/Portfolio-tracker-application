package com.portfolio.service;

import com.portfolio.model.Stock;
import com.portfolio.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Value("${alpha_vantage.api.key}")
    private String apiKey;

    private static final String API_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&apikey=%s";

    // Add stock
    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    // Get all stocks
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    // Get stock by ticker
    public Stock getStockByTicker(String ticker) {
        return stockRepository.findById(ticker).orElse(null);
    }

    // Update stock
    public Stock updateStock(String ticker, Stock updatedStock) {
        Stock existingStock = stockRepository.findById(ticker).orElse(null);
        if (existingStock != null) {
            existingStock.setQuantity(updatedStock.getQuantity());
            existingStock.setBuyPrice(updatedStock.getBuyPrice());
            return stockRepository.save(existingStock);
        }
        return null;
    }

    // Delete stock
    public void deleteStock(String ticker) {
        stockRepository.deleteById(ticker);
    }

    // Calculate total portfolio value
    public double getTotalPortfolioValue() {
        double totalValue = 0.0;
        for (Stock stock : getAllStocks()) {
            double currentPrice = getRealTimeStockPrice(stock.getTicker());
            totalValue += currentPrice * stock.getQuantity();
        }
        return totalValue;
    }

    // Get top-performing stock
    public Stock getTopPerformingStock() {
        List<Stock> stocks = getAllStocks();
        Stock topStock = null;
        double maxProfit = Double.MIN_VALUE;

        for (Stock stock : stocks) {
            double currentPrice = getRealTimeStockPrice(stock.getTicker());
            double profit = (currentPrice - stock.getBuyPrice()) * stock.getQuantity();
            if (profit > maxProfit) {
                maxProfit = profit;
                topStock = stock;
            }
        }
        return topStock;
    }

    // Get portfolio distribution
    public Map<String, Double> getPortfolioDistribution() {
        double totalValue = getTotalPortfolioValue();
        Map<String, Double> distribution = new HashMap<>();

        for (Stock stock : getAllStocks()) {
            double currentValue = getRealTimeStockPrice(stock.getTicker()) * stock.getQuantity();
            distribution.put(stock.getTicker(), (currentValue / totalValue) * 100);
        }
        return distribution;
    }

    // Fetch real-time stock price
    private double getRealTimeStockPrice(String ticker) {
        String url = String.format(API_URL, ticker, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            return parseStockPrice(response);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Parse stock price from API response
    private double parseStockPrice(String response) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode timeSeriesNode = rootNode.path("Time Series (5min)");
        String latestTimeKey = timeSeriesNode.fieldNames().next();
        JsonNode latestDataNode = timeSeriesNode.path(latestTimeKey);
        return latestDataNode.path("4. close").asDouble();
    }
}
