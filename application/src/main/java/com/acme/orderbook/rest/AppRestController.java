package com.acme.orderbook.rest;

import com.acme.orderbook.model.Instrument;
import com.acme.orderbook.model.Order;
import com.acme.orderbook.model.Statistics;
import com.acme.orderbook.rest.model.AddExecutionParams;
import com.acme.orderbook.rest.model.AddOrderParams;
import com.acme.orderbook.service.OrderBookService;
import com.acme.orderbook.service.PerformanceMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by robertk on 6/8/2019.
 */
@RestController
@RequestMapping("/")
public class AppRestController {

    private final OrderBookService orderBookService;

    @Autowired
    public AppRestController(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "instruments")
    public ResponseEntity<?> getInstruments() {

        List<Instrument> instruments = orderBookService.getInstruments();
        return ResponseEntity.ok(instruments);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "order-book/{instrumentId}/open")
    public ResponseEntity<?> openOrderBook(
            @PathVariable("instrumentId") long instrumentId) {

        orderBookService.open(instrumentId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "order-book/{instrumentId}/close")
    public ResponseEntity<?> closeOrderBook(
            @PathVariable("instrumentId") long instrumentId) {

        orderBookService.close(instrumentId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "order-book/{instrumentId}/add-order")
    public ResponseEntity<?> addOrder(
            @PathVariable("instrumentId") long instrumentId,
            @RequestBody AddOrderParams p) {

        orderBookService.addOrder(instrumentId, p.getQuantity(), p.getLimitPrice());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "order/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable("orderId") long orderId) {

        Order order = orderBookService.getOrder(orderId);

        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "order-book/{instrumentId}/add-execution")
    public ResponseEntity<?> addExecution(
            @PathVariable("instrumentId") long instrumentId,
            @RequestBody AddExecutionParams p) {

        orderBookService.addExecution(instrumentId, p.getQuantity(), p.getPrice());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "order-book/{instrumentId}/is-executed")
    public boolean isExecuted(
            @PathVariable("instrumentId") long instrumentId) {

        return orderBookService.isExecuted(instrumentId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "order-book/{instrumentId}/statistics")
    public ResponseEntity<?> getOrderBookStatistics(
            @PathVariable("instrumentId") long instrumentId) {

        Statistics statistics = orderBookService.generateStatistics(instrumentId);
        return ResponseEntity.ok(statistics);
    }

    @RequestMapping(method = RequestMethod.GET, value = "application/performance-metrics")
    public ResponseEntity<?> getApplicationPerformanceMetrics() {

        PerformanceMetrics performanceMetrics = orderBookService.getPerformanceMetrics();
        return ResponseEntity.ok(performanceMetrics);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "test/graceful-shutdown")
    public String gracefulShutdownTest() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ie) {
            //
        }
        return "Process finished";
    }
}
