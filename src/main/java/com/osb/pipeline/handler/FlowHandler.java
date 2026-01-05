package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import com.osb.pipeline.model.FlowResponse;

/**
 * Common interface for all flow handlers.
 * Each flow handler implements flow-specific logging, validation, and processing.
 */
public interface FlowHandler {
    
    /**
     * Get the queue name that this handler supports.
     * @return the queue name
     */
    String getQueueName();
    
    /**
     * Process the flow request.
     * @param request the flow request
     * @return the flow response
     */
    FlowResponse process(FlowRequest request);
    
    /**
     * Validate the request before processing.
     * @param request the flow request
     * @throws IllegalArgumentException if validation fails
     */
    void validate(FlowRequest request);
    
    /**
     * Log flow-specific information.
     * @param message the message to log
     * @param request the flow request
     */
    void logFlowInfo(String message, FlowRequest request);
}
