package com.osb.pipeline.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * Represents a single stage in a pipeline
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageConfiguration {
    
    private String name;
    private String type;
    private String description;
    
    // For validation stage
    private String schema;
    private Map<String, Object> inlineSchema;
    
    // For transformation stage
    private List<TransformationConfiguration> transformations;
    
    // For enrichment stage
    private Map<String, Object> enrichmentData;
    private String mergeStrategy;
    
    // For routing stage
    private String routingKey;
}
