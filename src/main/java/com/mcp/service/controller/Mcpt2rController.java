package com.mcp.service.controller;

import com.mcp.service.model.McpRequest;
import com.mcp.service.model.McpResponse;
import com.mcp.service.service.Mcpt2rService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for T2R flow
 * This controller remains untouched and operational for existing T2R flows
 */
@Slf4j
@RestController
@RequestMapping("/api/t2r")
public class Mcpt2rController {
    
    @Autowired
    private Mcpt2rService mcpt2rService;
    
    /**
     * T2R endpoint
     */
    @PostMapping("/process")
    public ResponseEntity<McpResponse> processT2rRequest(@RequestBody McpRequest request) {
        log.info("Received T2R request with correlationId: {}", request.getCorrelationId());
        
        McpResponse response = mcpt2rService.processT2rRequest(request);
        
        HttpStatus status = "SUCCESS".equals(response.getStatus()) 
            ? HttpStatus.OK 
            : HttpStatus.INTERNAL_SERVER_ERROR;
        
        return ResponseEntity.status(status).body(response);
    }
}
