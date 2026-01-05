package com.osb.pipeline.factory;

import com.osb.pipeline.handler.FlowHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to dynamically select the correct FlowHandler based on queue name.
 */
@Component
public class FlowHandlerFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(FlowHandlerFactory.class);
    
    private final Map<String, FlowHandler> handlerMap = new HashMap<>();
    
    @Autowired
    public FlowHandlerFactory(List<FlowHandler> handlers) {
        // Initialize handler map with all available handlers
        for (FlowHandler handler : handlers) {
            handlerMap.put(handler.getQueueName(), handler);
            logger.info("Registered FlowHandler for queue: {}", handler.getQueueName());
        }
    }
    
    /**
     * Get the appropriate FlowHandler for the given queue name.
     * @param queueName the queue name from x-queue-name header
     * @return the corresponding FlowHandler
     * @throws IllegalArgumentException if no handler found for the queue
     */
    public FlowHandler getHandler(String queueName) {
        if (queueName == null || queueName.isEmpty()) {
            throw new IllegalArgumentException("Queue name cannot be null or empty");
        }
        
        FlowHandler handler = handlerMap.get(queueName);
        if (handler == null) {
            logger.error("No handler found for queue: {}", queueName);
            throw new IllegalArgumentException("No handler configured for queue: " + queueName);
        }
        
        logger.debug("Selected handler for queue: {}", queueName);
        return handler;
    }
    
    /**
     * Check if a handler exists for the given queue name.
     * @param queueName the queue name
     * @return true if handler exists, false otherwise
     */
    public boolean hasHandler(String queueName) {
        return handlerMap.containsKey(queueName);
    }
}
