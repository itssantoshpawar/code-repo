package com.mcp.service.service;

import com.mcp.service.config.RoutingMapConfiguration;
import com.mcp.service.config.RoutingQueryConfiguration;
import com.mcp.service.model.McpRequest;
import com.mcp.service.model.McpResponse;
import com.mcp.service.model.RoutingDetails;
import com.mcp.service.util.TransformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Original T2R Service - handles T2R flow
 * This service remains untouched and operational for existing flows
 */
@Slf4j
@Service
public class Mcpt2rService {
    
    @Autowired
    private RoutingMapConfiguration routingMapConfiguration;
    
    @Autowired
    private RoutingQueryConfiguration routingQueryConfiguration;
    
    @Autowired
    private TransformationService transformationService;
    
    /**
     * Process T2R request
     */
    public McpResponse processT2rRequest(McpRequest request) {
        log.info("Processing T2R request with correlationId: {}", request.getCorrelationId());
        
        try {
            // Get routing details for T2R flow
            RoutingDetails routingDetails = routingMapConfiguration.getRoutingDetails("t2r");
            
            if (routingDetails == null) {
                log.error("No routing details found for T2R flow");
                return buildErrorResponse(request.getCorrelationId(), "Routing configuration not found");
            }
            
            // Execute routing query
            String query = routingQueryConfiguration.getQuery("t2r");
            log.debug("Executing routing query: {}", query);
            
            // Transform payload if required
            String transformedPayload = request.getPayload();
            if (routingDetails.isRequiresTransformation()) {
                transformedPayload = transformationService.transform(
                    request.getPayload(), 
                    routingDetails.getTransformationType()
                );
                log.debug("Payload transformed using type: {}", routingDetails.getTransformationType());
            }
            
            // Route to target endpoint
            log.info("Routing to endpoint: {}", routingDetails.getTargetEndpoint());
            
            // Simulate processing
            return McpResponse.builder()
                    .status("SUCCESS")
                    .message("T2R request processed successfully")
                    .correlationId(request.getCorrelationId())
                    .data(transformedPayload)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error processing T2R request: {}", e.getMessage(), e);
            return buildErrorResponse(request.getCorrelationId(), e.getMessage());
        }
    }
    
    private McpResponse buildErrorResponse(String correlationId, String errorMessage) {
        return McpResponse.builder()
                .status("ERROR")
                .message(errorMessage)
                .correlationId(correlationId)
                .build();
    }
}
