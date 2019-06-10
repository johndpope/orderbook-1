package com.acme.orderbook.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Created by robertk on 6/10/2019.
 */
public class OrderTest {

    private Order limitOrder;
    private Order marketOrder;
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        limitOrder = new Order(1, 1, LocalDateTime.now(), 90, 49.5);
        marketOrder = new Order(1, 2, LocalDateTime.now(), 50, null);
    }

    @Test
    public void testAddPartialExecutionLimitOrder() {
        assertTrue(limitOrder.isLimitOrder());
        assertFalse(limitOrder.isExecuted());

        assertTrue(limitOrder.isValid(49.4));
        assertTrue(limitOrder.isValid(49.5));
        assertFalse(limitOrder.isValid(49.6));

        assertEquals(90, limitOrder.getUnexecutedQuantity());

        limitOrder.addPartialExecution(15, 48.3);
        assertEquals(75, limitOrder.getUnexecutedQuantity());
        assertFalse(limitOrder.isExecuted());
        assertEquals(1, limitOrder.getPartialExecutions().size());

        limitOrder.addPartialExecution(30, 47.9);
        assertEquals(45, limitOrder.getUnexecutedQuantity());
        assertFalse(limitOrder.isExecuted());
        assertEquals(2, limitOrder.getPartialExecutions().size());

        limitOrder.addPartialExecution(45, 47.8);
        assertEquals(0, limitOrder.getUnexecutedQuantity());
        assertTrue(limitOrder.isExecuted());
        assertEquals(3, limitOrder.getPartialExecutions().size());

        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("order already executed");
        limitOrder.addPartialExecution(20, 47.7);

        assertEquals(0, limitOrder.getUnexecutedQuantity());
        assertTrue(limitOrder.isExecuted());
        assertEquals(47.9, limitOrder.getPartialExecutions().get(1).getPrice(), 0.001);
        assertEquals(45, limitOrder.getPartialExecutions().get(2).getQuantity());
    }

    @Test
    public void testAddPartialExecutionLimitOrder_wrongQuantity() {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("invalid partial quantity");
        limitOrder.addPartialExecution(100, 48.3);
    }

    @Test
    public void testAddPartialExecutionLimitOrder_wrongPrice() {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("invalid partial price");
        limitOrder.addPartialExecution(10, 55.3);
    }

    @Test
    public void testAddPartialExecutionMarketOrder() {
        assertFalse(marketOrder.isLimitOrder());
        assertFalse(marketOrder.isExecuted());

        assertTrue(marketOrder.isValid(13.4));
        assertTrue(marketOrder.isValid(49.5));
        assertTrue(marketOrder.isValid(72.6));

        assertEquals(50, marketOrder.getUnexecutedQuantity());

        marketOrder.addPartialExecution(10, 48.3);
        assertEquals(40, marketOrder.getUnexecutedQuantity());
        assertFalse(marketOrder.isExecuted());
        assertEquals(1, marketOrder.getPartialExecutions().size());

        marketOrder.addPartialExecution(5, 49.5);
        assertEquals(35, marketOrder.getUnexecutedQuantity());
        assertFalse(marketOrder.isExecuted());
        assertEquals(2, marketOrder.getPartialExecutions().size());

        marketOrder.addPartialExecution(35, 72.6);
        assertEquals(0, marketOrder.getUnexecutedQuantity());
        assertTrue(marketOrder.isExecuted());
        assertEquals(3, marketOrder.getPartialExecutions().size());

        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("order already executed");
        marketOrder.addPartialExecution(20, 47.7);

        assertEquals(0, marketOrder.getUnexecutedQuantity());
        assertTrue(marketOrder.isExecuted());
        assertEquals(49.5, marketOrder.getPartialExecutions().get(1).getPrice(), 0.001);
        assertEquals(35, marketOrder.getPartialExecutions().get(2).getQuantity());
    }
}
