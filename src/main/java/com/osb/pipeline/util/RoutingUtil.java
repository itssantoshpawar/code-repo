package com.osb.pipeline.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility class for common routing logic across all flows.
 */
@Component
public class RoutingUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(RoutingUtil.class);
    
    /**
     * Route the message based on content.
     * This is a shared utility that can be used by all flow handlers.
     * @param payload the message payload
     * @return the routing destination
     */
    public String routeMessage(String payload) {
        logger.debug("Routing message with payload length: {}", payload != null ? payload.length() : 0);
        
        // Common routing logic
        if (payload == null || payload.isEmpty()) {
            return "error-queue";
        }
        
        // Additional routing logic based on payload content
        if (payload.contains("urgent")) {
            return "priority-queue";
        }
        
        return "standard-queue";
    }
    
    /**
     * Validate routing path.
     * @param destination the routing destination
     * @return true if valid, false otherwise
     */
    public boolean validateRoutingPath(String destination) {
        return destination != null && !destination.isEmpty();
    }
}
