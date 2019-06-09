package com.acme.orderbook.service;

import com.acme.orderbook.book.OrderBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by robertk on 6/9/2019.
 */
@Service
public class OrderBookService {

    private final OrderBook orderBook;
    private final PerformanceMetrics performanceMetrics;

    @Value("${app.instrumentIds}")
    private String instrumentIds;

    @Autowired
    public OrderBookService(OrderBook orderBook, PerformanceMetrics performanceMetrics) {
        this.orderBook = orderBook;
        this.performanceMetrics = performanceMetrics;

        init();
    }

    private void init() {
        // TODO initialize instruments
    }

    public PerformanceMetrics getPerformanceMetrics() {
        return performanceMetrics;
    }
}
