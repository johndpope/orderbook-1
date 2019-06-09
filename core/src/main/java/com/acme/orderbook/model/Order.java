package com.acme.orderbook.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertk on 6/8/2019.
 */
public class Order {

    private final long instrumentId;
    private final long orderId;
    private final LocalDateTime entryDate;
    private final int quantity;
    private final Double limitPrice;

    private final List<OrderPartialExecution> partialExecutions = new ArrayList<>();

    public Order(long instrumentId, long orderId, LocalDateTime entryDate, int quantity, Double limitPrice) {
        this.instrumentId = instrumentId;
        this.orderId = orderId;
        this.entryDate = entryDate;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
    }

    private boolean isMarketOrder() {
        return limitPrice == null;
    }

    private int executedQuantity() {
        return partialExecutions.stream().mapToInt(OrderPartialExecution::getQuantity).sum();
    }

    public boolean isValid(double executionPrice) {
        return isMarketOrder() || limitPrice >= executionPrice;
    }

    public void addPartialExecution(int partialQuantity, double partialPrice) {
        if (partialQuantity > (quantity - executedQuantity())) {
            throw new IllegalStateException("invalid partial quantity " + partialQuantity + " for order " + this);
        }

        if (!isValid(partialPrice)) {
            throw new IllegalStateException("invalid partial price " + partialPrice + " for order " + this);
        }

        partialExecutions.add(new OrderPartialExecution(partialQuantity, partialPrice));
    }

    public List<OrderPartialExecution> getPartialExecutions() {
        return partialExecutions;
    }

    public long getInstrumentId() {
        return instrumentId;
    }

    public long getOrderId() {
        return orderId;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "instrumentId=" + instrumentId +
                ", orderId=" + orderId +
                ", entryDate=" + entryDate +
                ", quantity=" + quantity +
                ", limitPrice=" + limitPrice +
                ", executedQuantity=" + executedQuantity() +
                '}';
    }
}
