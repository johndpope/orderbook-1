package com.acme.orderbook.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalLong;

/**
 * Created by robertk on 6/8/2019.
 */
@Component
public class PerformanceMetrics {

    private final List<Long> orderProcessingTimes = new ArrayList<>(); // millis

    void addOrderProcessingTimeMillis(long millis) {
        orderProcessingTimes.add(millis);
    }

    public int getNumberOrdersProcessed() {
        return orderProcessingTimes.size();
    }

    public Long getMinOrderProcessingTimeMillis() {
        OptionalLong optional = orderProcessingTimes.stream().mapToLong(pt -> pt).min();
        return optional.isPresent() ? optional.getAsLong() : null;
    }

    public Long getMaxOrderProcessingTimeMillis() {
        OptionalLong optional = orderProcessingTimes.stream().mapToLong(pt -> pt).max();
        return optional.isPresent() ? optional.getAsLong() : null;
    }

    public Long getAverageOrderProcessingTimeMillis() {
        OptionalDouble optional = orderProcessingTimes.stream().mapToLong(pt -> pt).average();
        return optional.isPresent() ? (long) optional.getAsDouble() : null;
    }

}
