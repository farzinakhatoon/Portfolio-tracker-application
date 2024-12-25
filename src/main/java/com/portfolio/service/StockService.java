package com.portfolio.service;

import com.portfolio.model.Stock;
import com.portfolio.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

  
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    private static final String API_KEY = "50ZA1HDKN8G0WOCD";
    private static final String API_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&apikey=" + API_KEY;

    // Add a new stock
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

    // Update stock information
    public Stock updateStock(String ticker, Stock updatedStock) {
        Stock stock = stockRepository.findById(ticker).orElse(null);
        if (stock != null) {
            stock.setQuantity(updatedStock.getQuantity());
            stock.setBuyPrice(updatedStock.getBuyPrice());
            return stockRepository.save(stock);
        }
        return null;
    }

    // Delete stock by ticker
    public void deleteStock(String ticker) {
        stockRepository.deleteById(ticker);
    }

    // Fetch real-time stock price
    public double getRealTimeStockPrice(String ticker) {
        String url = String.format(API_URL, ticker);
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Get stock data from Alpha Vantage API
            String response = restTemplate.getForObject(url, String.class);
            // Extract the stock price from the response JSON
            // Example: Parse JSON to get the latest stock price (you may need to use a JSON parser like Jackson)
            return parseStockPrice(response);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Helper method to parse the real-time stock price

private double parseStockPrice(String response) {
    try {
        // Initialize Jackson's ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Parse the JSON response
        JsonNode rootNode = objectMapper.readTree(response);

        // Navigate to the relevant node containing the time series data
        JsonNode timeSeriesNode = rootNode.path("Time Series (5min)");
        
        // Get the latest entry (the first key in the object)
        String latestTimeKey = timeSeriesNode.fieldNames().next();
        JsonNode latestDataNode = timeSeriesNode.path(latestTimeKey);

        // Extract the "close" price from the latest data node
        double latestPrice = latestDataNode.path("4. close").asDouble();

        return latestPrice; // Return the parsed price
    } catch (Exception e) {
        e.printStackTrace();
        return 0.0; // Default to 0.0 in case of an error
    }
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
}

