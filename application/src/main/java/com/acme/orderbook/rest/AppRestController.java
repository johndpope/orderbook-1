package com.acme.orderbook.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by robertk on 6/8/2019.
 */
@RestController
@RequestMapping("/")
public class AppRestController {

    @RequestMapping("graceful-shutdown-test")
    public String gracefulShutdownTest() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ie) {
            //
        }
        return "Process finished";
    }
}
