package com.osb.pipeline.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility class for common transformation logic across all flows.
 */
@Component
public class TransformationUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(TransformationUtil.class);
    
    /**
     * Transform the payload by adding common metadata.
     * This is a shared utility that can be used by all flow handlers.
     * @param payload the original payload
     * @param requestId the request ID
     * @return the transformed payload
     */
    public String transformPayload(String payload, String requestId) {
        logger.debug("Transforming payload for request: {}", requestId);
        
        if (payload == null) {
            return "";
        }
        
        // Add common transformation logic
        String transformed = payload.trim();
        
        // Escape JSON special characters in the payload
        String escapedPayload = escapeJson(transformed);
        
        // Add metadata wrapper
        return String.format("{\"requestId\":\"%s\",\"data\":\"%s\"}", 
            escapeJson(requestId), escapedPayload);
    }
    
    /**
     * Escape special characters for JSON string.
     * @param input the input string
     * @return the escaped string
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
    
    /**
     * Enrich the payload with additional information.
     * @param payload the payload to enrich
     * @param enrichmentData additional data to add
     * @return the enriched payload
     */
    public String enrichPayload(String payload, String enrichmentData) {
        logger.debug("Enriching payload");
        
        if (payload == null) {
            return enrichmentData;
        }
        
        if (enrichmentData == null) {
            return payload;
        }
        
        return payload + enrichmentData;
    }
}
