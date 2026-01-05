package com.osb.pipeline.factory;

import com.osb.pipeline.handler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FlowHandlerFactoryTest {
    
    private FlowHandlerFactory factory;
    
    @BeforeEach
    void setUp() {
        Flow1Handler flow1Handler = new Flow1Handler();
        Flow2Handler flow2Handler = new Flow2Handler();
        Flow3Handler flow3Handler = new Flow3Handler();
        Flow4Handler flow4Handler = new Flow4Handler();
        Flow5Handler flow5Handler = new Flow5Handler();
        Flow6Handler flow6Handler = new Flow6Handler();
        
        factory = new FlowHandlerFactory(Arrays.asList(
            flow1Handler, flow2Handler, flow3Handler, 
            flow4Handler, flow5Handler, flow6Handler
        ));
    }
    
    @Test
    void testGetHandlerFlow1() {
        FlowHandler handler = factory.getHandler("flow1-queue");
        assertNotNull(handler);
        assertEquals("flow1-queue", handler.getQueueName());
    }
    
    @Test
    void testGetHandlerFlow2() {
        FlowHandler handler = factory.getHandler("flow2-queue");
        assertNotNull(handler);
        assertEquals("flow2-queue", handler.getQueueName());
    }
    
    @Test
    void testGetHandlerFlow3() {
        FlowHandler handler = factory.getHandler("flow3-queue");
        assertNotNull(handler);
        assertEquals("flow3-queue", handler.getQueueName());
    }
    
    @Test
    void testGetHandlerFlow4() {
        FlowHandler handler = factory.getHandler("flow4-queue");
        assertNotNull(handler);
        assertEquals("flow4-queue", handler.getQueueName());
    }
    
    @Test
    void testGetHandlerFlow5() {
        FlowHandler handler = factory.getHandler("flow5-queue");
        assertNotNull(handler);
        assertEquals("flow5-queue", handler.getQueueName());
    }
    
    @Test
    void testGetHandlerFlow6() {
        FlowHandler handler = factory.getHandler("flow6-queue");
        assertNotNull(handler);
        assertEquals("flow6-queue", handler.getQueueName());
    }
    
    @Test
    void testGetHandlerInvalidQueue() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> factory.getHandler("invalid-queue"));
        
        assertTrue(exception.getMessage().contains("No handler configured"));
    }
    
    @Test
    void testGetHandlerNullQueue() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> factory.getHandler(null));
        
        assertTrue(exception.getMessage().contains("cannot be null"));
    }
    
    @Test
    void testHasHandler() {
        assertTrue(factory.hasHandler("flow1-queue"));
        assertTrue(factory.hasHandler("flow2-queue"));
        assertTrue(factory.hasHandler("flow3-queue"));
        assertTrue(factory.hasHandler("flow4-queue"));
        assertTrue(factory.hasHandler("flow5-queue"));
        assertTrue(factory.hasHandler("flow6-queue"));
        assertFalse(factory.hasHandler("invalid-queue"));
    }
}
