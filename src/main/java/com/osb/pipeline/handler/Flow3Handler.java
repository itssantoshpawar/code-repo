package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Flow handler for Queue 3.
 * Handles specific validation and processing for Flow 3.
 */
@Component
public class Flow3Handler extends AbstractFlowHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Flow3Handler.class);
    private static final String QUEUE_NAME = "flow3-queue";
    
    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
    
    @Override
    protected String processFlowSpecific(FlowRequest request) {
        logger.debug("Processing Flow 3 specific logic");
        
        // Flow 3 specific processing
        String payload = request.getPayload();
        
        // Add Flow 3 specific transformation - add prefix
        String processed = "[FLOW3] " + payload;
        
        logFlowInfo("Flow 3 processing: payload prefixed", request);
        
        return processed;
    }
    
    @Override
    protected void validateFlowSpecific(FlowRequest request) {
        // Flow 3 specific validation
        if (request.getPayload().length() > 1000) {
            throw new IllegalArgumentException("Flow 3: Payload must not exceed 1000 characters");
        }
        
        logFlowInfo("Flow 3 validation passed", request);
    }
}
