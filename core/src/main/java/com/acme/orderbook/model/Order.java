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

    private LocalDateTime executionDate;

    private final List<OrderPartialExecution> partialExecutions = new ArrayList<>();

    public Order(long instrumentId, long orderId, LocalDateTime entryDate, int quantity, Double limitPrice) {
        this.instrumentId = instrumentId;
        this.orderId = orderId;
        this.entryDate = entryDate;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
    }

    public boolean isLimitOrder() {
        return limitPrice != null;
    }

    public int getExecutedQuantity() {
        return partialExecutions.stream().mapToInt(OrderPartialExecution::getQuantity).sum();
    }

    public int getUnexecutedQuantity() {
        return quantity - getExecutedQuantity();
    }

    public boolean isValid(double executionPrice) {
        return !isLimitOrder() || limitPrice >= executionPrice;
    }

    public boolean isExecuted() {
        return quantity == getExecutedQuantity();
    }

    public void addPartialExecution(int partialQuantity, double partialPrice) {
        if (isExecuted()) {
            throw new IllegalStateException("order already executed " + this);

        } else if (partialQuantity > getUnexecutedQuantity()) {
            throw new IllegalStateException("invalid partial quantity " + partialQuantity + " for order " + this);

        } else if (!isValid(partialPrice)) {
            throw new IllegalStateException("invalid partial price " + partialPrice + " for order " + this);
        }

        partialExecutions.add(new OrderPartialExecution(partialQuantity, partialPrice));
        if (isExecuted()) {
            executionDate = LocalDateTime.now();
        }
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

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "instrumentId=" + instrumentId +
                ", orderId=" + orderId +
                ", entryDate=" + entryDate +
                ", quantity=" + quantity +
                ", limitPrice=" + limitPrice +
                ", executedQuantity=" + getExecutedQuantity() +
                ", executionDate=" + executionDate +
                '}';
    }
}
