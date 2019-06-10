package com.acme.orderbook.book;

import com.acme.orderbook.model.Execution;
import com.acme.orderbook.model.Order;
import com.acme.orderbook.model.Statistics;

/**
 * Created by robertk on 6/8/2019.
 */
public interface OrderBook {
    void open();
    void close();

    void addOrder(Order order);

    void addExecution(Execution execution);
    boolean isExecuted();

    Statistics generateStatistics();
}
