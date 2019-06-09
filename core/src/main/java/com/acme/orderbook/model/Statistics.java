package com.acme.orderbook.model;

import java.time.LocalDateTime;
import java.util.TreeMap;

/**
 * Created by robertk on 6/8/2019.
 */
public class Statistics {
    private final long instrumentId;

    private int numberOrders;

    private int numberValidOrders;
    private int numberInvalidOrders;

    private int validDemand;
    private int invalidDemand;

    private int largestOrderQuantity;
    private int smallestOrderQuantity;

    private LocalDateTime firstOrderEntry;
    private LocalDateTime lastOrderEntry;

    private final TreeMap<Double, Integer> activeOrderLimitBreakDownTable = new TreeMap<>();

    private long accumulatedExecutionQuantity;
    private double lastExecutionPrice;

    public Statistics(long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public long getInstrumentId() {
        return instrumentId;
    }

    public int getNumberOrders() {
        return numberOrders;
    }

    public void setNumberOrders(int numberOrders) {
        this.numberOrders = numberOrders;
    }

    public int getNumberValidOrders() {
        return numberValidOrders;
    }

    public void setNumberValidOrders(int numberValidOrders) {
        this.numberValidOrders = numberValidOrders;
    }

    public int getNumberInvalidOrders() {
        return numberInvalidOrders;
    }

    public void setNumberInvalidOrders(int numberInvalidOrders) {
        this.numberInvalidOrders = numberInvalidOrders;
    }

    public int getValidDemand() {
        return validDemand;
    }

    public void setValidDemand(int validDemand) {
        this.validDemand = validDemand;
    }

    public int getInvalidDemand() {
        return invalidDemand;
    }

    public void setInvalidDemand(int invalidDemand) {
        this.invalidDemand = invalidDemand;
    }

    public int getLargestOrderQuantity() {
        return largestOrderQuantity;
    }

    public void setLargestOrderQuantity(int largestOrderQuantity) {
        this.largestOrderQuantity = largestOrderQuantity;
    }

    public int getSmallestOrderQuantity() {
        return smallestOrderQuantity;
    }

    public void setSmallestOrderQuantity(int smallestOrderQuantity) {
        this.smallestOrderQuantity = smallestOrderQuantity;
    }

    public LocalDateTime getFirstOrderEntry() {
        return firstOrderEntry;
    }

    public void setFirstOrderEntry(LocalDateTime firstOrderEntry) {
        this.firstOrderEntry = firstOrderEntry;
    }

    public LocalDateTime getLastOrderEntry() {
        return lastOrderEntry;
    }

    public void setLastOrderEntry(LocalDateTime lastOrderEntry) {
        this.lastOrderEntry = lastOrderEntry;
    }

    public TreeMap<Double, Integer> getActiveOrderLimitBreakDownTable() {
        return activeOrderLimitBreakDownTable;
    }

    public long getAccumulatedExecutionQuantity() {
        return accumulatedExecutionQuantity;
    }

    public void setAccumulatedExecutionQuantity(long accumulatedExecutionQuantity) {
        this.accumulatedExecutionQuantity = accumulatedExecutionQuantity;
    }

    public double getLastExecutionPrice() {
        return lastExecutionPrice;
    }

    public void setLastExecutionPrice(double lastExecutionPrice) {
        this.lastExecutionPrice = lastExecutionPrice;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "instrumentId=" + instrumentId +
                ", numberOrders=" + numberOrders +
                ", numberValidOrders=" + numberValidOrders +
                ", numberInvalidOrders=" + numberInvalidOrders +
                ", validDemand=" + validDemand +
                ", invalidDemand=" + invalidDemand +
                ", largestOrderQuantity=" + largestOrderQuantity +
                ", smallestOrderQuantity=" + smallestOrderQuantity +
                ", firstOrderEntry=" + firstOrderEntry +
                ", lastOrderEntry=" + lastOrderEntry +
                ", activeOrderLimitBreakDownTable=" + activeOrderLimitBreakDownTable +
                ", accumulatedExecutionQuantity=" + accumulatedExecutionQuantity +
                ", lastExecutionPrice=" + lastExecutionPrice +
                '}';
    }
}
