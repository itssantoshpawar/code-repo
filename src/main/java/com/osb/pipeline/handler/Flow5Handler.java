package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Flow handler for Queue 5.
 * Handles specific validation and processing for Flow 5.
 */
@Component
public class Flow5Handler extends AbstractFlowHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Flow5Handler.class);
    private static final String QUEUE_NAME = "flow5-queue";
    
    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
    
    @Override
    protected String processFlowSpecific(FlowRequest request) {
        logger.debug("Processing Flow 5 specific logic");
        
        // Flow 5 specific processing
        String payload = request.getPayload();
        
        // Add Flow 5 specific transformation - add suffix
        String processed = payload + " [FLOW5]";
        
        logFlowInfo("Flow 5 processing: payload suffixed", request);
        
        return processed;
    }
    
    @Override
    protected void validateFlowSpecific(FlowRequest request) {
        // Flow 5 specific validation
        if (!request.getPayload().contains("data")) {
            throw new IllegalArgumentException("Flow 5: Payload must contain 'data' keyword");
        }
        
        logFlowInfo("Flow 5 validation passed", request);
    }
}
