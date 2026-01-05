package com.mcp.service.config;

import com.mcp.service.model.RoutingDetails;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RoutingMapConfiguration {
    
    private final Map<String, RoutingDetails> routingMap = new HashMap<>();
    
    public RoutingMapConfiguration() {
        initializeRoutingMap();
    }
    
    private void initializeRoutingMap() {
        // T2R Flow (original Mcpt2rService flow)
        routingMap.put("t2r", RoutingDetails.builder()
                .flowType("t2r")
                .domain("default")
                .queueName("t2r-queue")
                .targetEndpoint("/api/t2r/process")
                .transformationType("standard")
                .requiresTransformation(true)
                .build());
        
        // B2B Flow (domain 1)
        routingMap.put("b2b-domain1", RoutingDetails.builder()
                .flowType("b2b")
                .domain("domain1")
                .queueName("b2b-domain1-queue")
                .targetEndpoint("/api/b2b/process")
                .transformationType("b2b")
                .requiresTransformation(true)
                .build());
        
        // Siebel Flow (domain 1)
        routingMap.put("siebel-domain1", RoutingDetails.builder()
                .flowType("siebel")
                .domain("domain1")
                .queueName("siebel-domain1-queue")
                .targetEndpoint("/api/siebel/process")
                .transformationType("siebel")
                .requiresTransformation(true)
                .build());
        
        // Portal Flow
        routingMap.put("portal", RoutingDetails.builder()
                .flowType("portal")
                .domain("default")
                .queueName("portal-queue")
                .targetEndpoint("/api/portal/process")
                .transformationType("portal")
                .requiresTransformation(true)
                .build());
        
        // Flow
        routingMap.put("flow", RoutingDetails.builder()
                .flowType("flow")
                .domain("default")
                .queueName("flow-queue")
                .targetEndpoint("/api/flow/process")
                .transformationType("flow")
                .requiresTransformation(true)
                .build());
        
        // NEO Flow
        routingMap.put("neo", RoutingDetails.builder()
                .flowType("neo")
                .domain("default")
                .queueName("neo-queue")
                .targetEndpoint("/api/neo/process")
                .transformationType("neo")
                .requiresTransformation(true)
                .build());
        
        // Sync Flow (non-queue HTTP adapter)
        routingMap.put("sync", RoutingDetails.builder()
                .flowType("sync")
                .domain("default")
                .queueName(null)  // No queue for sync flow
                .targetEndpoint("/api/sync/process")
                .transformationType("sync")
                .requiresTransformation(false)
                .build());
    }
    
    public RoutingDetails getRoutingDetails(String flowType, String domain) {
        String key = domain != null && !domain.isEmpty() 
                ? flowType + "-" + domain 
                : flowType;
        return routingMap.getOrDefault(key, routingMap.get(flowType));
    }
    
    public RoutingDetails getRoutingDetails(String flowType) {
        return getRoutingDetails(flowType, null);
    }
}
