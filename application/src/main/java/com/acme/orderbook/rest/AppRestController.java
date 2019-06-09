package com.acme.orderbook.rest;

import com.acme.orderbook.service.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(method = RequestMethod.PUT, value = "test/graceful-shutdown")
    public String gracefulShutdownTest() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ie) {
            //
        }
        return "Process finished";
    }

    @RequestMapping(method = RequestMethod.PUT, value = "order-book/{instrumentId}/open")
    public ResponseEntity<?> openOrderBook(
            @PathVariable("instrumentId") long instrumentId) {

        // TODO
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "order-book/{instrumentId}/close")
    public ResponseEntity<?> closeOrderBook(
            @PathVariable("instrumentId") long instrumentId) {

        // TODO
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "order-book/{instrumentId}/statistics")
    public ResponseEntity<?> getOrderBookStatistics(
            @PathVariable("instrumentId") long instrumentId) {

        // TODO
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "order/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable("orderId") long orderId) {

        // TODO
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "application/performance-metrics")
    public ResponseEntity<?> getApplicationPerformanceMetrics() {

        // TODO
        return ResponseEntity.ok().build();
    }
}
