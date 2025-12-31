package com.osb.pipeline.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * Represents a pipeline flow configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowConfiguration {
    
    private String flowName;
    private String description;
    private String version;
    private List<StageConfiguration> stages;
    private ErrorHandlingConfiguration errorHandling;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorHandlingConfiguration {
        private String onError;
        private Integer retryCount;
    }
}
