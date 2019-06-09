package com.acme.orderbook.model;

/**
 * Created by robertk on 6/8/2019.
 */
public class Instrument {

    private final long id;

    public Instrument(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
