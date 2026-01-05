package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Flow handler for Queue 1.
 * Handles specific validation and processing for Flow 1.
 */
@Component
public class Flow1Handler extends AbstractFlowHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Flow1Handler.class);
    private static final String QUEUE_NAME = "flow1-queue";
    
    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
    
    @Override
    protected String processFlowSpecific(FlowRequest request) {
        logger.debug("Processing Flow 1 specific logic");
        
        // Flow 1 specific processing
        String payload = request.getPayload();
        
        // Add Flow 1 specific metadata
        String processed = payload.replaceAll("\\s+", " ").trim();
        
        logFlowInfo("Flow 1 processing: payload normalized", request);
        
        return processed;
    }
    
    @Override
    protected void validateFlowSpecific(FlowRequest request) {
        // Flow 1 specific validation
        if (request.getPayload().length() < 10) {
            throw new IllegalArgumentException("Flow 1: Payload must be at least 10 characters");
        }
        
        logFlowInfo("Flow 1 validation passed", request);
    }
}
