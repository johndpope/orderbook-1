package com.acme.orderbook.common;

/**
 * Created by robertk on 6/9/2019.
 */
public class OrderBookUtil {

    public static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
}
