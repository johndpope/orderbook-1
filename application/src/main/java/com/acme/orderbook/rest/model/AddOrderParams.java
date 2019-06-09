package com.acme.orderbook.rest.model;

/**
 * Created by robertk on 6/9/2019.
 */
public class AddOrderParams {
    private int quantity;
    private Double limitPrice;

    public int getQuantity() {
        return quantity;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }
}
