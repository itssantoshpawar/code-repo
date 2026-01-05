package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Flow handler for Queue 4.
 * Handles specific validation and processing for Flow 4.
 */
@Component
public class Flow4Handler extends AbstractFlowHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Flow4Handler.class);
    private static final String QUEUE_NAME = "flow4-queue";
    
    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
    
    @Override
    protected String processFlowSpecific(FlowRequest request) {
        logger.debug("Processing Flow 4 specific logic");
        
        // Flow 4 specific processing
        String payload = request.getPayload();
        
        // Add Flow 4 specific transformation - reverse string
        String processed = new StringBuilder(payload).reverse().toString();
        
        logFlowInfo("Flow 4 processing: payload reversed", request);
        
        return processed;
    }
    
    @Override
    protected void validateFlowSpecific(FlowRequest request) {
        // Flow 4 specific validation
        if (request.getHeaders() == null || request.getHeaders().isEmpty()) {
            throw new IllegalArgumentException("Flow 4: Headers are required");
        }
        
        logFlowInfo("Flow 4 validation passed", request);
    }
}
