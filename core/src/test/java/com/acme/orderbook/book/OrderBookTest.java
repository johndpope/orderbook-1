package com.acme.orderbook.book;

import com.acme.orderbook.model.Execution;
import com.acme.orderbook.model.Order;
import com.acme.orderbook.model.Statistics;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by robertk on 6/10/2019.
 */
public class OrderBookTest {
    private static final Logger log = LoggerFactory.getLogger(OrderBookTest.class);

    private OrderBookImpl orderBook;
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        orderBook = new OrderBookImpl(1);
    }

    @Test
    public void testOpenClose() {
        assertTrue(orderBook.isOpen());
        assertFalse(orderBook.isExecuted());

        // open again
        orderBook.open();
        assertTrue(orderBook.isOpen());

        orderBook.close();
        assertFalse(orderBook.isOpen());

        // close again
        orderBook.close();
        assertFalse(orderBook.isOpen());
    }

    @Test
    public void testAddOrder() {
        assertTrue(orderBook.isOpen());
        assertFalse(orderBook.isExecuted());

        assertEquals(0, orderBook.getActiveValidOrders(49.5).size());
        assertEquals(0, orderBook.getActiveInvalidOrders(49.5).size());
        assertEquals(0, orderBook.getActiveValidOrdersDemand(49.5));
        assertEquals(0, orderBook.getActiveInvalidOrdersDemand(49.5));
        assertNull(orderBook.getLastExecution());

        // add orders to the open book
        orderBook.addOrder(new Order(1, 1, LocalDateTime.now(), 90, 49.5));

        assertEquals(1, orderBook.getActiveValidOrders(49.5).size());
        assertEquals(0, orderBook.getActiveInvalidOrders(49.5).size());
        assertEquals(0, orderBook.getActiveValidOrders(49.6).size());
        assertEquals(1, orderBook.getActiveInvalidOrders(49.6).size());

        // close the book
        orderBook.close();
        assertFalse(orderBook.isOpen());
        assertFalse(orderBook.isExecuted());

        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("cannot add orders to closed book");
        orderBook.addOrder(new Order(1, 2, LocalDateTime.now(), 90, 49.2));
    }

    @Test
    public void testCalculateOrderPartialExecutionQuantities() {
        List<Integer> demands = Arrays.asList(5, 13, 17, 9, 15, 2, 23, 11, 4, 34);

        // case 1 - partially executed
        int executionQuantity = 48;
        List<Integer> partialExecutionQuantities = orderBook.calculateOrderPartialExecutionQuantities(demands, executionQuantity);
        List<Integer> expectedQuantities = Arrays.asList(2, 5, 7, 4, 6, 1, 9, 4, 2, 8); // calculated in excel

        for (int i = 0; i < demands.size(); i++) {
            assertEquals(expectedQuantities.get(i), partialExecutionQuantities.get(i));
        }

        // case 2 - all orders fully executed
        executionQuantity = 150;
        partialExecutionQuantities = orderBook.calculateOrderPartialExecutionQuantities(demands, executionQuantity);
        expectedQuantities = Arrays.asList(5, 13, 17, 9, 15, 2, 23, 11, 4,34); // all orders fully executed

        for (int i = 0; i < demands.size(); i++) {
            assertEquals(expectedQuantities.get(i), partialExecutionQuantities.get(i));
        }

        // case 3 - partially executed, some zero executions
        executionQuantity = 12;
        partialExecutionQuantities = orderBook.calculateOrderPartialExecutionQuantities(demands, executionQuantity);
        expectedQuantities = Arrays.asList(1, 2, 2, 1, 2, 1, 3, 0, 0, 0); // calculated in excel

        for (int i = 0; i < demands.size(); i++) {
            assertEquals(expectedQuantities.get(i), partialExecutionQuantities.get(i));
        }
    }

    @Test
    public void testAddExecution() {
        assertTrue(orderBook.isOpen());
        assertFalse(orderBook.isExecuted());

        // add orders to an open book
        orderBook.addOrder(new Order(1, 1, LocalDateTime.now(), 15, 48.5));
        orderBook.addOrder(new Order(1, 2, LocalDateTime.now(), 10, 49.5));
        orderBook.addOrder(new Order(1, 3, LocalDateTime.now(), 20, 50.5));

        assertEquals(3, orderBook.getActiveOrderMap().size());
        assertEquals(2, orderBook.getActiveValidOrders(49.5).size());
        assertEquals(1, orderBook.getActiveInvalidOrders(49.5).size());
        assertEquals(30, orderBook.getActiveValidOrdersDemand(49.5));
        assertEquals(15, orderBook.getActiveInvalidOrdersDemand(49.5));

        // close the book and add an execution
        orderBook.close();
        assertFalse(orderBook.isOpen());

        orderBook.addExecution(new Execution(1, 10, 49.5));
        assertFalse(orderBook.isExecuted());

        assertEquals(3, orderBook.getActiveOrderMap().size());
        assertEquals(0, orderBook.getExecutedOrderMap().size());
        assertEquals(0, orderBook.getCanceledOrderMap().size());

        assertEquals(0, orderBook.getActiveOrderMap().get(1L).getExecutedQuantity());
        assertFalse(orderBook.getActiveOrderMap().get(1L).isExecuted());

        assertEquals(4, orderBook.getActiveOrderMap().get(2L).getExecutedQuantity());
        assertFalse(orderBook.getActiveOrderMap().get(2L).isExecuted());

        assertEquals(6, orderBook.getActiveOrderMap().get(3L).getExecutedQuantity());
        assertFalse(orderBook.getActiveOrderMap().get(3L).isExecuted());

        // add a second execution
        orderBook.addExecution(new Execution(1, 20, 49.5));
        assertTrue(orderBook.isExecuted());

        assertEquals(0, orderBook.getActiveOrderMap().size());
        assertEquals(2, orderBook.getExecutedOrderMap().size());
        assertEquals(1, orderBook.getCanceledOrderMap().size());

        assertEquals(0, orderBook.getCanceledOrderMap().get(1L).getExecutedQuantity());
        assertFalse(orderBook.getCanceledOrderMap().get(1L).isExecuted());

        assertEquals(10, orderBook.getExecutedOrderMap().get(2L).getExecutedQuantity());
        assertTrue(orderBook.getExecutedOrderMap().get(2L).isExecuted());

        assertEquals(20, orderBook.getExecutedOrderMap().get(3L).getExecutedQuantity());
        assertTrue(orderBook.getExecutedOrderMap().get(3L).isExecuted());

        // try to add an execution to the already executed book
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("cannot add execution to already executed book");
        orderBook.addExecution(new Execution(1, 30, 49.5));

        // reopen the book again and add a new order
        orderBook.open();
        orderBook.addOrder(new Order(1, 4, LocalDateTime.now(), 20, 50.5));
        assertFalse(orderBook.isExecuted());

        // close the book again and add a new execution
        orderBook.close();
        orderBook.addExecution(new Execution(1, 20, 49.5));
        assertTrue(orderBook.isExecuted());

        assertEquals(0, orderBook.getActiveOrderMap().size());
        assertEquals(3, orderBook.getExecutedOrderMap().size());
        assertEquals(1, orderBook.getCanceledOrderMap().size());
    }

    @Test
    public void testGenerateStatistics() {
        // add some orders to an open book
        orderBook.addOrder(new Order(1, 1, LocalDateTime.now(), 15, 48.5));
        orderBook.addOrder(new Order(1, 2, LocalDateTime.now(), 10, 49.5));
        orderBook.addOrder(new Order(1, 3, LocalDateTime.now(), 20, 50.5));

        // close the book and add an execution
        orderBook.close();
        orderBook.addExecution(new Execution(1, 10, 49.5));

        // generate statistics
        Statistics statistics = orderBook.generateStatistics();
        log.info(statistics.toString());

        assertEquals(3, statistics.getActiveOrders());
        assertEquals(0, statistics.getExecutedOrders());
        assertEquals(0, statistics.getCanceledOrders());

        assertEquals(2, statistics.getActiveValidOrders());
        assertEquals(1, statistics.getActiveInvalidOrders());

        assertEquals(49.5, statistics.getLastExecutionPrice(), 0.0001);
        assertEquals(20, statistics.getActiveValidOrdersDemand());
        assertEquals(15, statistics.getActiveInvalidOrdersDemand());

        assertEquals(20, statistics.getLargestActiveOrderQuantity());
        assertEquals(10, statistics.getSmallestActiveOrderQuantity());

        Map<Double, Integer> t = statistics.getActiveOrderLimitBreakDownTable();
        assertEquals(15, t.get(48.5).intValue());
        assertEquals(6, t.get(49.5).intValue());
        assertEquals(14, t.get(50.5).intValue());

        // add another execution to fully execute all orders
        orderBook.addExecution(new Execution(1, 20, 49.5));
        statistics = orderBook.generateStatistics();
        log.info(statistics.toString());

        assertEquals(0, statistics.getActiveOrders());
        assertEquals(2, statistics.getExecutedOrders());
        assertEquals(1, statistics.getCanceledOrders());

        assertEquals(0, statistics.getActiveValidOrders());
        assertEquals(0, statistics.getActiveInvalidOrders());

        assertEquals(49.5, statistics.getLastExecutionPrice(), 0.0001);
        assertEquals(0, statistics.getActiveValidOrdersDemand());
        assertEquals(0, statistics.getActiveInvalidOrdersDemand());

        assertEquals(0, statistics.getLargestActiveOrderQuantity());
        assertEquals(0, statistics.getSmallestActiveOrderQuantity());

        t = statistics.getActiveOrderLimitBreakDownTable();
        assertTrue(t.isEmpty());
    }
}
