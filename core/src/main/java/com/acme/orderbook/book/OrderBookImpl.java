package com.acme.orderbook.book;

import com.acme.orderbook.common.OrderBookUtil;
import com.acme.orderbook.model.Execution;
import com.acme.orderbook.model.Order;
import com.acme.orderbook.model.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private AtomicBoolean executed = new AtomicBoolean(false);

    private final ConcurrentMap<Long, Order> activeOrderMap = new ConcurrentHashMap<>(); // orderId -> order
    private final ConcurrentMap<Long, Order> executedOrderMap = new ConcurrentHashMap<>(); // orderId -> order
    private final ConcurrentMap<Long, Order> canceledOrderMap = new ConcurrentHashMap<>(); // orderId -> order
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
    public boolean isOpen() {
        return open.get();
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

        double executionPrice = execution.getPrice();
        int executionQuantity = execution.getQuantity();

        if (!isOpen()) {
            checkIfBookExecuted(executionPrice);

            if (!isExecuted()) {
                executions.add(execution);
                List<Order> validOrders = getActiveValidOrders(executionPrice);
                List<Order> invalidOrders = getActiveInvalidOrders(executionPrice);

                List<Integer> demands = validOrders.stream().map(Order::getUnexecutedQuantity).collect(Collectors.toList());
                List<Integer> orderPartialExecutionQuantities = calculateOrderPartialExecutionQuantities(demands, executionQuantity);

                for (int i = 0; i < orderPartialExecutionQuantities.size(); i++) {
                    validOrders.get(i).addPartialExecution(orderPartialExecutionQuantities.get(i), executionPrice);
                }

                for (Order order : invalidOrders) {
                    order.addPartialExecution(0, executionPrice);
                }

                validOrders.stream().filter(Order::isExecuted).forEach(o -> {
                    activeOrderMap.remove(o.getOrderId());
                    executedOrderMap.put(o.getOrderId(), o);
                });

                checkIfBookExecuted(executionPrice);
            } else {
                throw new IllegalStateException("cannot add execution to already executed book " + instrumentId);
            }
        } else {
            throw new IllegalStateException("cannot add execution to open book " + instrumentId);
        }
    }

    List<Integer> calculateOrderPartialExecutionQuantities(List<Integer> demands, int quantityToDistribute) {
        int cumulativeDemand = demands.stream().mapToInt(d -> d).sum();
        List<Integer> partialExecutionQuantities = new ArrayList<>();

        int distributedQuantity = 0;

        for (int demand : demands) {
            double pctToApply = (double) demand / (double) cumulativeDemand;
            int partialExecutionQuantity = OrderBookUtil.min(quantityToDistribute - distributedQuantity, demand, (int) Math.ceil(pctToApply * quantityToDistribute));

            partialExecutionQuantities.add(partialExecutionQuantity);
            distributedQuantity += partialExecutionQuantity;
        }
        return partialExecutionQuantities;
    }

    private void checkIfBookExecuted(double executionPrice) {
        if (!isExecuted()) {
            int validOrdersDemand = getActiveValidOrdersDemand(executionPrice);

            if (validOrdersDemand == 0) {
                executed.set(true);
                activeOrderMap.values().forEach(o -> canceledOrderMap.put(o.getOrderId(), o));
                activeOrderMap.clear();
            }
        }
    }

    @Override
    public boolean isExecuted() {
        return executed.get();
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

        activeOrderMap.values().stream().mapToInt(Order::getQuantity).max().ifPresent(s::setLargestActiveOrderQuantity);
        activeOrderMap.values().stream().mapToInt(Order::getQuantity).min().ifPresent(s::setSmallestActiveOrderQuantity);

        activeOrderMap.values().stream().map(Order::getEntryDate).min(Comparator.comparing(ldt -> ldt.toEpochSecond(ZoneOffset.UTC))).ifPresent(s::setFirstActiveOrderEntry);
        activeOrderMap.values().stream().map(Order::getEntryDate).max(Comparator.comparing(ldt -> ldt.toEpochSecond(ZoneOffset.UTC))).ifPresent(s::setLastActiveOrderEntry);

        for (Order order : activeOrderMap.values()) {
            if (order.isLimitOrder()) {
                int orderDemand = order.getUnexecutedQuantity();
                double limitPrice = order.getLimitPrice();

                Map<Double, Integer> t = s.getActiveOrderLimitBreakDownTable();

                Integer currentCumulativeDemandPerLimitPrice = t.get(limitPrice);
                int newCumulativeDemandPerLimitPrice = currentCumulativeDemandPerLimitPrice != null ? currentCumulativeDemandPerLimitPrice + orderDemand : orderDemand;

                t.put(limitPrice, newCumulativeDemandPerLimitPrice);
            }
        }
        return s;
    }

    List<Order> getActiveValidOrders(double price) {
        return activeOrderMap.values().stream().filter(o -> o.isValid(price)).collect(Collectors.toList());
    }

    List<Order> getActiveInvalidOrders(double price) {
        return activeOrderMap.values().stream().filter(o -> !o.isValid(price)).collect(Collectors.toList());
    }

    int getActiveValidOrdersDemand(double price) {
        return activeOrderMap.values().stream().filter(o -> o.isValid(price)).mapToInt(Order::getUnexecutedQuantity).sum();
    }

    int getActiveInvalidOrdersDemand(double price) {
        return activeOrderMap.values().stream().filter(o -> !o.isValid(price)).mapToInt(Order::getUnexecutedQuantity).sum();
    }

    Execution getLastExecution() {
        return executions.size() > 0 ? executions.get(executions.size() - 1) : null;
    }

    Map<Long, Order> getActiveOrderMap() {
        return activeOrderMap;
    }

    Map<Long, Order> getExecutedOrderMap() {
        return executedOrderMap;
    }

    Map<Long, Order> getCanceledOrderMap() {
        return canceledOrderMap;
    }

    private void validate(long instrumentId) {
        if (this.instrumentId != instrumentId) {
            throw new IllegalStateException("wrong order book " + this.instrumentId + " != " + instrumentId);
        }
    }
}
