package com.acme.orderbook.book;

import com.acme.orderbook.model.Order;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by robertk on 6/10/2019.
 */
public class OrderBookTest {

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

        orderBook.addOrder(new Order(1, 1, LocalDateTime.now(), 90, 49.5));

        assertEquals(1, orderBook.getActiveValidOrders(49.5).size());
        assertEquals(0, orderBook.getActiveInvalidOrders(49.5).size());
        assertEquals(0, orderBook.getActiveValidOrders(49.6).size());
        assertEquals(1, orderBook.getActiveInvalidOrders(49.6).size());

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
        expectedQuantities = Arrays.asList(1, 2, 2, 1, 2, 1, 3, 0, 0, 0); // all orders fully executed

        for (int i = 0; i < demands.size(); i++) {
            assertEquals(expectedQuantities.get(i), partialExecutionQuantities.get(i));
        }
    }
}
