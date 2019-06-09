package com.acme.orderbook.model;

/**
 * Created by robertk on 6/8/2019.
 */
public interface OrderBook {
    long getInstrumentId();

    void open();
    void close();

    void addOrder(Order order);
    Order getOrder(long orderId);

    void addExecution(Execution execution);
    boolean isExecuted();

    Statistics generateStatistics();
}
