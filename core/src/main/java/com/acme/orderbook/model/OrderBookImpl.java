package com.acme.orderbook.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by robertk on 6/8/2019.
 */
public class OrderBookImpl implements OrderBook {
    private static final Logger log = LoggerFactory.getLogger(OrderBookImpl.class);

    private final long instrumentId;

    private AtomicBoolean open = new AtomicBoolean(true);
    private final ConcurrentMap<Long, Order> activeOrderMap = new ConcurrentHashMap<>(); // orderId -> order
    private final ConcurrentMap<Long, Order> executedOrderMap = new ConcurrentHashMap<>(); // orderId -> order
    private final ConcurrentMap<Long, Order> canceledOrderMap = new ConcurrentHashMap<>(); // orderId -> order
    private final List<Execution> executions = new ArrayList<>();

    private AtomicBoolean executed = new AtomicBoolean(false);

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

                List<Order> validOrders = getActiveValidOrders((execution.getPrice()));
                int validOrdersDemand = getActiveValidOrdersDemand(execution.getPrice());

                if (validOrdersDemand == 0) {
                    executed.set(true);
                    activeOrderMap.values().forEach(o -> canceledOrderMap.put(o.getOrderId(), o));
                    activeOrderMap.clear();

                    return;
                }

                int appliedQuantity = 0;
                for (Order order : validOrders) {
                    // TODO
                    if (appliedQuantity == execution.getQuantity()) {
                        break;
                    }
                }

                validOrders.stream().filter(Order::isExecuted).forEach(o -> {
                    activeOrderMap.remove(o.getOrderId());
                    executedOrderMap.put(o.getOrderId(), o);
                });
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
        return executed.get();
    }

    private List<Order> getActiveValidOrders(double price) {
        return activeOrderMap.values().stream().filter(o -> o.isValid(price)).collect(Collectors.toList());
    }

    private List<Order> getActiveInvalidOrders(double price) {
        return activeOrderMap.values().stream().filter(o -> !o.isValid(price)).collect(Collectors.toList());
    }

    private int getActiveValidOrdersDemand(double price) {
        return activeOrderMap.values().stream().filter(o -> o.isValid(price)).mapToInt(Order::getUnexecutedQuantity).sum();
    }

    private int getActiveInvalidOrdersDemand(double price) {
        return activeOrderMap.values().stream().filter(o -> !o.isValid(price)).mapToInt(Order::getUnexecutedQuantity).sum();
    }

    private Execution getLastExecution() {
        return executions.size() > 0 ? executions.get(executions.size() - 1) : null;
    }

    @Override
    public Statistics generateStatistics() {
        Statistics s = new Statistics(instrumentId);

        s.setActiveOrders(activeOrderMap.size());
        s.setExecutedOrders(executedOrderMap.size());
        s.setCanceledOrders(canceledOrderMap.size());

        if (getLastExecution() != null) {
            double lastExecutionPrice = getLastExecution().getPrice();

            s.setLastExecutionPrice(lastExecutionPrice);

            s.setActiveValidOrders(getActiveValidOrders(lastExecutionPrice).size());
            s.setActiveInvalidOrders(getActiveInvalidOrders(lastExecutionPrice).size());

            s.setActiveValidOrdersDemand(getActiveValidOrdersDemand(lastExecutionPrice));
            s.setActiveInvalidOrdersDemand(getActiveInvalidOrdersDemand(lastExecutionPrice));
        }

        OptionalInt largestActiveOrderQuantityOptional = activeOrderMap.values().stream().mapToInt(Order::getQuantity).max();
        if (largestActiveOrderQuantityOptional.isPresent()) {
            s.setLargestActiveOrderQuantity(largestActiveOrderQuantityOptional.getAsInt());
        }

        OptionalInt smallestActiveOrderQuantityOptional = activeOrderMap.values().stream().mapToInt(Order::getQuantity).min();
        if (smallestActiveOrderQuantityOptional.isPresent()) {
            s.setLargestActiveOrderQuantity(smallestActiveOrderQuantityOptional.getAsInt());
        }

        Optional<LocalDateTime> firstActiveOrderEntryOptional = activeOrderMap.values().stream().map(Order::getEntryDate).min(Comparator.comparing(ldt -> ldt.toEpochSecond(ZoneOffset.UTC)));
        firstActiveOrderEntryOptional.ifPresent(s::setFirstActiveOrderEntry);

        Optional<LocalDateTime> lastActiveOrderEntryOptional = activeOrderMap.values().stream().map(Order::getEntryDate).max(Comparator.comparing(ldt -> ldt.toEpochSecond(ZoneOffset.UTC)));
        lastActiveOrderEntryOptional.ifPresent(s::setLastActiveOrderEntry);

        for (Order order : activeOrderMap.values()) {
            if (order.isLimitOrder()) {
                int orderDemand = order.getUnexecutedQuantity();
                double limitPrice = order.getLimitPrice();

                Map<Double, Integer> t = s.getActiveOrderLimitBreakDownTable();

                if (t.containsKey(limitPrice)) {
                    t.put(limitPrice, t.get(limitPrice) + orderDemand);
                } else {
                    t.put(limitPrice, orderDemand);
                }
            }
        }
        return s;
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
