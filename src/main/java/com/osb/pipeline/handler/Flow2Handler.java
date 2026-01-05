package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Flow handler for Queue 2.
 * Handles specific validation and processing for Flow 2.
 */
@Component
public class Flow2Handler extends AbstractFlowHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Flow2Handler.class);
    private static final String QUEUE_NAME = "flow2-queue";
    
    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
    
    @Override
    protected String processFlowSpecific(FlowRequest request) {
        logger.debug("Processing Flow 2 specific logic");
        
        // Flow 2 specific processing
        String payload = request.getPayload();
        
        // Add Flow 2 specific transformation - convert to uppercase
        String processed = payload.toUpperCase();
        
        logFlowInfo("Flow 2 processing: payload converted to uppercase", request);
        
        return processed;
    }
    
    @Override
    protected void validateFlowSpecific(FlowRequest request) {
        // Flow 2 specific validation
        if (!request.getPayload().matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("Flow 2: Payload must contain at least one letter");
        }
        
        logFlowInfo("Flow 2 validation passed", request);
    }
}
