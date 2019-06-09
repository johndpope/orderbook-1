package com.acme.orderbook.service;

import com.acme.orderbook.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by robertk on 6/9/2019.
 */
@Service
public class OrderBookService {

    private final PerformanceMetrics performanceMetrics;
    private final Map<Long, OrderBook> orderBooks = new HashMap<>();
    private final ConcurrentMap<Long, Order> allOrderMap = new ConcurrentHashMap<>(); // orderId -> order

    @Value("${instrumentIds}")
    private String instrumentIds;

    private final AtomicLong orderIdGenerator = new AtomicLong();

    @Autowired
    public OrderBookService(PerformanceMetrics performanceMetrics) {
        this.performanceMetrics = performanceMetrics;

        for (String instrumentIdStr : instrumentIds.split(",")) {
            long instrumentId = Long.valueOf(instrumentIdStr);
            OrderBook orderBook = new OrderBookImpl(instrumentId);
            orderBooks.put(instrumentId, orderBook);
        }
    }

    public PerformanceMetrics getPerformanceMetrics() {
        return performanceMetrics;
    }

    public void open(long instrumentId) {
        validate(instrumentId);
        orderBook(instrumentId).open();
    }

    public void close(long instrumentId) {
        validate(instrumentId);
        orderBook(instrumentId).close();
    }

    public void addOrder(long instrumentId, int quantity, Double limitPrice) {
        validate(instrumentId);

        long orderId = orderIdGenerator.incrementAndGet();
        Order order = new Order(instrumentId, orderId, LocalDateTime.now(), quantity, limitPrice);
        orderBook(instrumentId).addOrder(order);
    }

    public Order getOrder(long orderId) {
        return allOrderMap.get(orderId);
    }

    public void addExecution(long instrumentId, int quantity, double price) {
        validate(instrumentId);

        Execution execution = new Execution(instrumentId, quantity, price);
        orderBook(instrumentId).addExecution(execution);
    }

    public boolean isExecuted(long instrumentId) {
        validate(instrumentId);
        return orderBook(instrumentId).isExecuted();
    }

    public Statistics generateStatistics(long instrumentId) {
        validate(instrumentId);
        return orderBook(instrumentId).generateStatistics();
    }

    private void validate(long instrumentId) {
        if (!orderBooks.keySet().contains(instrumentId)) {
            throw new IllegalStateException("no order book available for instrument " + instrumentId);
        }
    }

    private OrderBook orderBook(long instrumentId) {
        return orderBooks.get(instrumentId);
    }
}
