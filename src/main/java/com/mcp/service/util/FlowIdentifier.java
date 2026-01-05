package com.mcp.service.util;

import org.springframework.stereotype.Component;

@Component
public class FlowIdentifier {
    
    /**
     * Identify flow type from queue name
     */
    public String identifyFlowFromQueueName(String queueName) {
        if (queueName == null || queueName.isEmpty()) {
            return null;
        }
        
        String lowerQueueName = queueName.toLowerCase();
        
        if (lowerQueueName.contains("t2r")) {
            return "t2r";
        } else if (lowerQueueName.contains("b2b")) {
            return "b2b";
        } else if (lowerQueueName.contains("siebel")) {
            return "siebel";
        } else if (lowerQueueName.contains("portal")) {
            return "portal";
        } else if (lowerQueueName.contains("flow")) {
            return "flow";
        } else if (lowerQueueName.contains("neo")) {
            return "neo";
        } else if (lowerQueueName.contains("sync")) {
            return "sync";
        }
        
        return null;
    }
    
    /**
     * Extract domain from queue name
     */
    public String extractDomainFromQueueName(String queueName) {
        if (queueName == null || queueName.isEmpty()) {
            return null;
        }
        
        String lowerQueueName = queueName.toLowerCase();
        
        if (lowerQueueName.contains("domain1")) {
            return "domain1";
        } else if (lowerQueueName.contains("domain2")) {
            return "domain2";
        }
        
        return null;
    }
    
    /**
     * Identify flow type from request headers
     */
    public String identifyFlowFromHeaders(String flowTypeHeader, String xFlowType) {
        if (flowTypeHeader != null && !flowTypeHeader.isEmpty()) {
            return flowTypeHeader.toLowerCase();
        }
        if (xFlowType != null && !xFlowType.isEmpty()) {
            return xFlowType.toLowerCase();
        }
        return null;
    }
}
