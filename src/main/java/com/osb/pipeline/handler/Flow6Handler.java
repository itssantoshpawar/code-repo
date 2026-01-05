package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Flow handler for Queue 6.
 * Handles specific validation and processing for Flow 6.
 */
@Component
public class Flow6Handler extends AbstractFlowHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Flow6Handler.class);
    private static final String QUEUE_NAME = "flow6-queue";
    
    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
    
    @Override
    protected String processFlowSpecific(FlowRequest request) {
        logger.debug("Processing Flow 6 specific logic");
        
        // Flow 6 specific processing
        String payload = request.getPayload();
        
        // Add Flow 6 specific transformation - replace spaces with underscores and convert to lowercase
        String processed = payload.replace(" ", "_").toLowerCase();
        
        logFlowInfo("Flow 6 processing: payload transformed", request);
        
        return processed;
    }
    
    @Override
    protected void validateFlowSpecific(FlowRequest request) {
        // Flow 6 specific validation
        String payload = request.getPayload();
        if (payload.length() < 5 || payload.length() > 500) {
            throw new IllegalArgumentException("Flow 6: Payload must be between 5 and 500 characters");
        }
        
        logFlowInfo("Flow 6 validation passed", request);
    }
}
