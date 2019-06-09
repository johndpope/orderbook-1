package com.acme.orderbook.rest.model;

/**
 * Created by robertk on 6/9/2019.
 */
public class AddExecutionParams {
    private int quantity;
    private double price;

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
