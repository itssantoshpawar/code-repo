package com.osb.pipeline.controller;

import com.osb.pipeline.factory.FlowHandlerFactory;
import com.osb.pipeline.handler.FlowHandler;
import com.osb.pipeline.model.FlowRequest;
import com.osb.pipeline.model.FlowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for OSB pipeline flows.
 * Delegates processing to appropriate FlowHandler based on x-queue-name header.
 */
@RestController
@RequestMapping("/api/flow")
public class FlowController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlowController.class);
    
    @Autowired
    private FlowHandlerFactory flowHandlerFactory;
    
    /**
     * Process a flow request.
     * @param queueName the queue name from x-queue-name header
     * @param payload the request payload
     * @param headers all request headers
     * @return the flow response
     */
    @PostMapping("/process")
    public ResponseEntity<FlowResponse> processFlow(
            @RequestHeader(value = "x-queue-name", required = true) String queueName,
            @RequestBody String payload,
            @RequestHeader Map<String, String> headers) {
        
        String requestId = UUID.randomUUID().toString();
        
        // Validate queueName input to prevent injection attacks
        if (queueName == null || queueName.isEmpty() || !isValidQueueName(queueName)) {
            logger.warn("Invalid queue name received: {}", queueName);
            FlowResponse errorResponse = FlowResponse.builder()
                .status("ERROR")
                .message("Invalid queue name format")
                .requestId(requestId)
                .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        logger.info("Received request for queue: {}, requestId: {}", queueName, requestId);
        
        try {
            // Get the appropriate handler from factory
            FlowHandler handler = flowHandlerFactory.getHandler(queueName);
            
            // Build the request
            FlowRequest request = FlowRequest.builder()
                .queueName(queueName)
                .payload(payload)
                .headers(headers)
                .requestId(requestId)
                .build();
            
            // Process the request
            FlowResponse response = handler.process(request);
            
            logger.info("Successfully processed request: {}", requestId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error for request {}: {}", requestId, e.getMessage());
            FlowResponse errorResponse = FlowResponse.builder()
                .status("ERROR")
                .message(e.getMessage())
                .requestId(requestId)
                .build();
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Error processing request {}: {}", requestId, e.getMessage(), e);
            FlowResponse errorResponse = FlowResponse.builder()
                .status("ERROR")
                .message("Internal server error: " + e.getMessage())
                .requestId(requestId)
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint.
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
    
    /**
     * Validate queue name format to prevent injection attacks.
     * Queue names should only contain alphanumeric characters, hyphens, and underscores.
     * @param queueName the queue name to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidQueueName(String queueName) {
        // Queue name should match pattern: alphanumeric, hyphens, underscores
        // Max length: 100 characters
        return queueName.matches("^[a-zA-Z0-9_-]{1,100}$");
    }
}
