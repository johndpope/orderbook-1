package com.acme.orderbook.book;

import com.acme.orderbook.model.Execution;
import com.acme.orderbook.model.Order;
import org.springframework.stereotype.Component;

/**
 * Created by robertk on 6/8/2019.
 */
@Component
public class OrderBookImpl implements OrderBook {

    @Override
    public void open(long instrumentId) {

    }

    @Override
    public void close(long instrumentId) {

    }

    @Override
    public void addOrder(Order order) {

    }

    @Override
    public Order getOrder(long orderId) {
        return null;
    }

    @Override
    public void addExecution(Execution execution) {

    }

    @Override
    public boolean isExecuted(long instrumentId) {
        return false;
    }

    @Override
    public Statistics getStatistics(long instrumentId) {
        return null;
    }
}
