package com.acme.orderbook.model;

/**
 * Created by robertk on 6/8/2019.
 */
public class Execution {

    private final long instrumentId;
    private final int quantity;
    private final double price;

    public Execution(long instrumentId, int quantity, double price) {
        this.instrumentId = instrumentId;
        this.quantity = quantity;
        this.price = price;
    }

    public long getInstrumentId() {
        return instrumentId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
