package com.osb.pipeline.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

/**
 * Represents a transformation configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransformationConfiguration {
    
    private String type;
    private Map<String, Object> params;
}
