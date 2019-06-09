package com.acme.orderbook.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by robertk on 6/8/2019.
 */
public class OrderBookImpl implements OrderBook {
    private static final Logger log = LoggerFactory.getLogger(OrderBookImpl.class);

    private final long instrumentId;

    private AtomicBoolean open = new AtomicBoolean(true);
    private final ConcurrentMap<Long, Order> activeOrderMap = new ConcurrentHashMap<>(); // orderId -> order
    private final ConcurrentMap<Long, Order> executedOrderMap = new ConcurrentHashMap<>(); // orderId -> order
    private final List<Execution> executions = new ArrayList<>();

    public OrderBookImpl(long instrumentId) {
        this.instrumentId = instrumentId;
    }

    @Override
    public void open() {
        if (!isOpen()) {
            open.set(true);
        } else {
            log.warn("book " + instrumentId + " already open");
        }
    }

    @Override
    public void close() {
        if (isOpen()) {
            open.set(false);
        } else {
            log.warn("book " + instrumentId + " already closed");
        }
    }

    @Override
    public void addOrder(Order order) {
        validate(order.getInstrumentId());
        if (isOpen()) {
            activeOrderMap.put(order.getOrderId(), order);
        } else {
            throw new IllegalStateException("cannot add orders to closed book " + instrumentId);
        }
    }

    @Override
    public void addExecution(Execution execution) {
        validate(execution.getInstrumentId());

        if (!isOpen()) {
            if (!isExecuted()) {
                executions.add(execution);
                // TODO calculate quantities and partially execute orders
                // TODO move executed orders from active to executed order map

            } else {
                throw new IllegalStateException("cannot add execution to already executed book " + instrumentId);
            }
        }
        if (open.get()) {
            throw new IllegalStateException("cannot add execution to closed book " + instrumentId);
        }
    }

    @Override
    public boolean isExecuted() {
        // TODO determine if it is executed already
        return false;
    }

    @Override
    public Statistics generateStatistics() {
        Statistics statistics = new Statistics(instrumentId);
        // TODO calculate statistics

        return statistics;
    }

    private boolean isOpen() {
        return open.get();
    }

    private void validate(long instrumentId) {
        if (this.instrumentId != instrumentId) {
            throw new IllegalStateException("wrong order book " + this.instrumentId + " != " + instrumentId);
        }
    }
}
