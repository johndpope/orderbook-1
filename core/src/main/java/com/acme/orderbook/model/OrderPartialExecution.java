package com.acme.orderbook.model;

/**
 * Created by robertk on 6/9/2019.
 */
public class OrderPartialExecution {

    private final int quantity;
    private final double price;

    public OrderPartialExecution(int quantity, double price) {
        this.quantity = quantity;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
