package com.acme.orderbook.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by robertk on 6/8/2019.
 */
public class Statistics {
    private final long instrumentId;

    private int activeOrders;
    private int executedOrders;
    private int canceledOrders;

    private int activeValidOrders;
    private int activeInvalidOrders;

    private double lastExecutionPrice;
    private int activeValidOrdersDemand;
    private int activeInvalidOrdersDemand;

    private int largestActiveOrderQuantity;
    private int smallestActiveOrderQuantity;

    private LocalDateTime firstActiveOrderEntry;
    private LocalDateTime lastActiveOrderEntry;

    private final Map<Double, Integer> activeOrderLimitBreakDownTable = new TreeMap<>();

    public Statistics(long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public long getInstrumentId() {
        return instrumentId;
    }

    public int getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(int activeOrders) {
        this.activeOrders = activeOrders;
    }

    public int getExecutedOrders() {
        return executedOrders;
    }

    public void setExecutedOrders(int executedOrders) {
        this.executedOrders = executedOrders;
    }

    public int getCanceledOrders() {
        return canceledOrders;
    }

    public void setCanceledOrders(int canceledOrders) {
        this.canceledOrders = canceledOrders;
    }

    public int getActiveValidOrders() {
        return activeValidOrders;
    }

    public void setActiveValidOrders(int activeValidOrders) {
        this.activeValidOrders = activeValidOrders;
    }

    public int getActiveInvalidOrders() {
        return activeInvalidOrders;
    }

    public void setActiveInvalidOrders(int activeInvalidOrders) {
        this.activeInvalidOrders = activeInvalidOrders;
    }

    public double getLastExecutionPrice() {
        return lastExecutionPrice;
    }

    public void setLastExecutionPrice(double lastExecutionPrice) {
        this.lastExecutionPrice = lastExecutionPrice;
    }

    public int getActiveValidOrdersDemand() {
        return activeValidOrdersDemand;
    }

    public void setActiveValidOrdersDemand(int activeValidOrdersDemand) {
        this.activeValidOrdersDemand = activeValidOrdersDemand;
    }

    public int getActiveInvalidOrdersDemand() {
        return activeInvalidOrdersDemand;
    }

    public void setActiveInvalidOrdersDemand(int activeInvalidOrdersDemand) {
        this.activeInvalidOrdersDemand = activeInvalidOrdersDemand;
    }

    public int getLargestActiveOrderQuantity() {
        return largestActiveOrderQuantity;
    }

    public void setLargestActiveOrderQuantity(int largestActiveOrderQuantity) {
        this.largestActiveOrderQuantity = largestActiveOrderQuantity;
    }

    public int getSmallestActiveOrderQuantity() {
        return smallestActiveOrderQuantity;
    }

    public void setSmallestActiveOrderQuantity(int smallestActiveOrderQuantity) {
        this.smallestActiveOrderQuantity = smallestActiveOrderQuantity;
    }

    public LocalDateTime getFirstActiveOrderEntry() {
        return firstActiveOrderEntry;
    }

    public void setFirstActiveOrderEntry(LocalDateTime firstActiveOrderEntry) {
        this.firstActiveOrderEntry = firstActiveOrderEntry;
    }

    public LocalDateTime getLastActiveOrderEntry() {
        return lastActiveOrderEntry;
    }

    public void setLastActiveOrderEntry(LocalDateTime lastActiveOrderEntry) {
        this.lastActiveOrderEntry = lastActiveOrderEntry;
    }

    public Map<Double, Integer> getActiveOrderLimitBreakDownTable() {
        return activeOrderLimitBreakDownTable;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "instrumentId=" + instrumentId +
                ", activeOrders=" + activeOrders +
                ", executedOrders=" + executedOrders +
                ", canceledOrders=" + canceledOrders +
                ", activeValidOrders=" + activeValidOrders +
                ", activeInvalidOrders=" + activeInvalidOrders +
                ", lastExecutionPrice=" + lastExecutionPrice +
                ", activeValidOrdersDemand=" + activeValidOrdersDemand +
                ", activeInvalidOrdersDemand=" + activeInvalidOrdersDemand +
                ", largestActiveOrderQuantity=" + largestActiveOrderQuantity +
                ", smallestActiveOrderQuantity=" + smallestActiveOrderQuantity +
                ", firstActiveOrderEntry=" + firstActiveOrderEntry +
                ", lastActiveOrderEntry=" + lastActiveOrderEntry +
                ", activeOrderLimitBreakDownTable=" + activeOrderLimitBreakDownTable +
                '}';
    }
}
