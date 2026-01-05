package com.mcp.service.controller;

import com.mcp.service.model.McpRequest;
import com.mcp.service.model.McpResponse;
import com.mcp.service.service.McpCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for MCP Common Service
 * Handles 6 additional flows: B2B, Siebel, Portal, Flow, NEO, and Sync
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp")
public class McpCommonController {
    
    @Autowired
    private McpCommonService mcpCommonService;
    
    /**
     * Common endpoint for B2B, Siebel, Portal, Flow, and NEO flows
     * Flow type is identified from x-queue-name header or flow-type header
     */
    @PostMapping("/process")
    public ResponseEntity<McpResponse> processRequest(
            @RequestBody McpRequest request,
            @RequestHeader(value = "x-queue-name", required = false) String xQueueName,
            @RequestHeader(value = "flow-type", required = false) String flowType) {
        
        log.info("Received MCP request with correlationId: {}, x-queue-name: {}, flow-type: {}", 
            request.getCorrelationId(), xQueueName, flowType);
        
        McpResponse response = mcpCommonService.processRequest(request, xQueueName, flowType);
        
        HttpStatus status = "SUCCESS".equals(response.getStatus()) 
            ? HttpStatus.OK 
            : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Dedicated endpoint for Sync flow (non-queue HTTP adapter)
     * This is a synchronous endpoint that processes requests immediately
     */
    @PostMapping("/sync")
    public ResponseEntity<McpResponse> processSyncRequest(@RequestBody McpRequest request) {
        log.info("Received SYNC request with correlationId: {}", request.getCorrelationId());
        
        // Set flow type to sync
        request.setFlowType("sync");
        
        McpResponse response = mcpCommonService.processRequest(request, null, "sync");
        
        HttpStatus status = "SUCCESS".equals(response.getStatus()) 
            ? HttpStatus.OK 
            : HttpStatus.INTERNAL_SERVER_ERROR;
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Endpoint for B2B flow
     */
    @PostMapping("/b2b")
    public ResponseEntity<McpResponse> processB2bRequest(
            @RequestBody McpRequest request,
            @RequestHeader(value = "x-queue-name", required = false) String xQueueName) {
        
        log.info("Received B2B request with correlationId: {}", request.getCorrelationId());
        request.setFlowType("b2b");
        
        McpResponse response = mcpCommonService.processRequest(request, xQueueName, "b2b");
        
        HttpStatus status = "SUCCESS".equals(response.getStatus()) 
            ? HttpStatus.OK 
            : HttpStatus.INTERNAL_SERVER_ERROR;
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Endpoint for Siebel flow
     */
    @PostMapping("/siebel")
    public ResponseEntity<McpResponse> processSiebelRequest(
            @RequestBody McpRequest request,
            @RequestHeader(value = "x-queue-name", required = false) String xQueueName) {
        
        log.info("Received Siebel request with correlationId: {}", request.getCorrelationId());
        request.setFlowType("siebel");
        
        McpResponse response = mcpCommonService.processRequest(request, xQueueName, "siebel");
        
        HttpStatus status = "SUCCESS".equals(response.getStatus()) 
            ? HttpStatus.OK 
            : HttpStatus.INTERNAL_SERVER_ERROR;
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Endpoint for Portal flow
     */
    @PostMapping("/portal")
    public ResponseEntity<McpResponse> processPortalRequest(
            @RequestBody McpRequest request,
            @RequestHeader(value = "x-queue-name", required = false) String xQueueName) {
        
        log.info("Received Portal request with correlationId: {}", request.getCorrelationId());
        request.setFlowType("portal");
        
        McpResponse response = mcpCommonService.processRequest(request, xQueueName, "portal");
        
        HttpStatus status = "SUCCESS".equals(response.getStatus()) 
            ? HttpStatus.OK 
            : HttpStatus.INTERNAL_SERVER_ERROR;
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Endpoint for Flow
     */
    @PostMapping("/flow")
    public ResponseEntity<McpResponse> processFlowRequest(
            @RequestBody McpRequest request,
            @RequestHeader(value = "x-queue-name", required = false) String xQueueName) {
        
        log.info("Received Flow request with correlationId: {}", request.getCorrelationId());
        request.setFlowType("flow");
        
        McpResponse response = mcpCommonService.processRequest(request, xQueueName, "flow");
        
        HttpStatus status = "SUCCESS".equals(response.getStatus()) 
            ? HttpStatus.OK 
            : HttpStatus.INTERNAL_SERVER_ERROR;
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Endpoint for NEO flow
     */
    @PostMapping("/neo")
    public ResponseEntity<McpResponse> processNeoRequest(
            @RequestBody McpRequest request,
            @RequestHeader(value = "x-queue-name", required = false) String xQueueName) {
        
        log.info("Received NEO request with correlationId: {}", request.getCorrelationId());
        request.setFlowType("neo");
        
        McpResponse response = mcpCommonService.processRequest(request, xQueueName, "neo");
        
        HttpStatus status = "SUCCESS".equals(response.getStatus()) 
            ? HttpStatus.OK 
            : HttpStatus.INTERNAL_SERVER_ERROR;
        
        return ResponseEntity.status(status).body(response);
    }
}
