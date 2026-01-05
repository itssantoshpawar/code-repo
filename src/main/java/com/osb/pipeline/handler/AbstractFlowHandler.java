package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import com.osb.pipeline.model.FlowResponse;
import com.osb.pipeline.util.RoutingUtil;
import com.osb.pipeline.util.TransformationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract base class for flow handlers.
 * Provides common functionality and delegates to subclasses for flow-specific behavior.
 */
public abstract class AbstractFlowHandler implements FlowHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractFlowHandler.class);
    
    @Autowired
    protected RoutingUtil routingUtil;
    
    @Autowired
    protected TransformationUtil transformationUtil;
    
    @Override
    public FlowResponse process(FlowRequest request) {
        logFlowInfo("Starting processing", request);
        
        // Validate request
        validate(request);
        
        // Perform flow-specific processing
        String processedPayload = processFlowSpecific(request);
        
        // Transform using common utility
        String transformedPayload = transformationUtil.transformPayload(
            processedPayload, 
            request.getRequestId()
        );
        
        // Route using common utility
        String destination = routingUtil.routeMessage(transformedPayload);
        
        logFlowInfo("Processing completed, destination: " + destination, request);
        
        return FlowResponse.builder()
            .status("SUCCESS")
            .message("Processed successfully for " + getQueueName())
            .transformedPayload(transformedPayload)
            .requestId(request.getRequestId())
            .build();
    }
    
    /**
     * Flow-specific processing logic to be implemented by each handler.
     * @param request the flow request
     * @return the processed payload
     */
    protected abstract String processFlowSpecific(FlowRequest request);
    
    @Override
    public void validate(FlowRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getPayload() == null || request.getPayload().isEmpty()) {
            throw new IllegalArgumentException("Payload cannot be null or empty");
        }
        if (request.getRequestId() == null || request.getRequestId().isEmpty()) {
            throw new IllegalArgumentException("Request ID cannot be null or empty");
        }
        
        // Delegate to flow-specific validation
        validateFlowSpecific(request);
    }
    
    /**
     * Flow-specific validation logic to be implemented by each handler.
     * @param request the flow request
     */
    protected abstract void validateFlowSpecific(FlowRequest request);
    
    @Override
    public void logFlowInfo(String message, FlowRequest request) {
        logger.info("[{}] [RequestId: {}] {}", 
            getQueueName(), 
            request.getRequestId(), 
            message);
    }
}
