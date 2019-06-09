package com.acme.orderbook.book;

import com.acme.orderbook.model.Execution;
import com.acme.orderbook.model.Order;

/**
 * Created by robertk on 6/8/2019.
 */
public interface OrderBook {
    void open(long instrumentId);
    void close(long instrumentId);

    void addOrder(Order order);
    Order getOrder(long orderId);

    void addExecution(Execution execution);
    boolean isExecuted(long instrumentId);

    Statistics getStatistics(long instrumentId);
}
