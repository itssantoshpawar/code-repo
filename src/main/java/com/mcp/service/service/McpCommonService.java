package com.mcp.service.service;

import com.mcp.service.config.RoutingMapConfiguration;
import com.mcp.service.config.RoutingQueryConfiguration;
import com.mcp.service.model.McpRequest;
import com.mcp.service.model.McpResponse;
import com.mcp.service.model.RoutingDetails;
import com.mcp.service.util.FlowIdentifier;
import com.mcp.service.util.TransformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Common MCP Service - handles 6 additional flows:
 * 1. B2B (domain 1)
 * 2. Siebel (domain 1)
 * 3. Portal
 * 4. Flow
 * 5. NEO
 * 6. Sync (non-queue HTTP adapter)
 * 
 * This service is self-contained and modular, reusing utilities from Mcpt2rService
 */
@Slf4j
@Service
public class McpCommonService {
    
    @Autowired
    private RoutingMapConfiguration routingMapConfiguration;
    
    @Autowired
    private RoutingQueryConfiguration routingQueryConfiguration;
    
    @Autowired
    private TransformationService transformationService;
    
    @Autowired
    private FlowIdentifier flowIdentifier;
    
    /**
     * Process request for any of the 6 supported flows
     * Flow type is determined from x-queue-name header or request headers
     */
    public McpResponse processRequest(McpRequest request, String xQueueName, String flowTypeHeader) {
        log.info("Processing MCP Common request with correlationId: {}", request.getCorrelationId());
        
        try {
            // Identify flow type from various sources
            String flowType = determineFlowType(request, xQueueName, flowTypeHeader);
            
            if (flowType == null || !isValidFlow(flowType)) {
                log.error("Invalid or unsupported flow type: {}", flowType);
                return buildErrorResponse(request.getCorrelationId(), "Invalid or unsupported flow type");
            }
            
            log.info("Identified flow type: {}", flowType);
            
            // Extract domain if applicable
            String domain = extractDomain(request, xQueueName);
            
            // Get routing details
            RoutingDetails routingDetails = domain != null 
                ? routingMapConfiguration.getRoutingDetails(flowType, domain)
                : routingMapConfiguration.getRoutingDetails(flowType);
            
            if (routingDetails == null) {
                log.error("No routing details found for flow: {} and domain: {}", flowType, domain);
                return buildErrorResponse(request.getCorrelationId(), "Routing configuration not found");
            }
            
            // Execute routing query
            String query = routingQueryConfiguration.getQuery(flowType);
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
            log.info("Routing to endpoint: {} for flow: {}", routingDetails.getTargetEndpoint(), flowType);
            
            // Handle sync flow differently (non-queue HTTP adapter)
            if ("sync".equals(flowType)) {
                return processSyncFlow(request, transformedPayload, routingDetails);
            }
            
            // Handle queue-based flows (B2B, Siebel, Portal, Flow, NEO)
            return processQueueBasedFlow(request, transformedPayload, routingDetails, flowType);
                    
        } catch (Exception e) {
            log.error("Error processing MCP Common request: {}", e.getMessage(), e);
            return buildErrorResponse(request.getCorrelationId(), e.getMessage());
        }
    }
    
    /**
     * Determine flow type from multiple sources
     */
    private String determineFlowType(McpRequest request, String xQueueName, String flowTypeHeader) {
        // Priority 1: Check request object
        if (request.getFlowType() != null && !request.getFlowType().isEmpty()) {
            return request.getFlowType().toLowerCase();
        }
        
        // Priority 2: Check x-queue-name header
        if (xQueueName != null && !xQueueName.isEmpty()) {
            String flowFromQueue = flowIdentifier.identifyFlowFromQueueName(xQueueName);
            if (flowFromQueue != null) {
                return flowFromQueue;
            }
        }
        
        // Priority 3: Check flow-type header
        String flowFromHeader = flowIdentifier.identifyFlowFromHeaders(flowTypeHeader, null);
        if (flowFromHeader != null) {
            return flowFromHeader;
        }
        
        return null;
    }
    
    /**
     * Extract domain from various sources
     */
    private String extractDomain(McpRequest request, String xQueueName) {
        // Check request object
        if (request.getDomain() != null && !request.getDomain().isEmpty()) {
            return request.getDomain();
        }
        
        // Check queue name
        if (xQueueName != null && !xQueueName.isEmpty()) {
            return flowIdentifier.extractDomainFromQueueName(xQueueName);
        }
        
        return null;
    }
    
    /**
     * Validate if flow type is one of the 6 supported flows
     */
    private boolean isValidFlow(String flowType) {
        return flowType != null && (
            flowType.equals("b2b") ||
            flowType.equals("siebel") ||
            flowType.equals("portal") ||
            flowType.equals("flow") ||
            flowType.equals("neo") ||
            flowType.equals("sync")
        );
    }
    
    /**
     * Process sync flow (non-queue HTTP adapter)
     */
    private McpResponse processSyncFlow(McpRequest request, String transformedPayload, RoutingDetails routingDetails) {
        log.info("Processing SYNC flow (non-queue HTTP adapter)");
        
        // Sync flow processes immediately without queuing
        return McpResponse.builder()
                .status("SUCCESS")
                .message("Sync request processed successfully")
                .correlationId(request.getCorrelationId())
                .data(transformedPayload)
                .build();
    }
    
    /**
     * Process queue-based flows (B2B, Siebel, Portal, Flow, NEO)
     */
    private McpResponse processQueueBasedFlow(McpRequest request, String transformedPayload, 
                                               RoutingDetails routingDetails, String flowType) {
        log.info("Processing queue-based flow: {} with queue: {}", flowType, routingDetails.getQueueName());
        
        // Send to queue for async processing
        // Simulate queue message send
        String message = String.format("%s request queued successfully to %s", 
            flowType.toUpperCase(), routingDetails.getQueueName());
        
        return McpResponse.builder()
                .status("SUCCESS")
                .message(message)
                .correlationId(request.getCorrelationId())
                .data(transformedPayload)
                .build();
    }
    
    private McpResponse buildErrorResponse(String correlationId, String errorMessage) {
        return McpResponse.builder()
                .status("ERROR")
                .message(errorMessage)
                .correlationId(correlationId)
                .build();
    }
}
