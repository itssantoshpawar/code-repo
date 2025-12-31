package com.osb.pipeline.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context object that carries state through pipeline execution
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineContext {
    
    private String flowName;
    private LocalDateTime executionStart;
    private LocalDateTime executionEnd;
    private String status;
    
    @Builder.Default
    private List<String> stagesCompleted = new ArrayList<>();
    
    private String failedStage;
    private String routingDecision;
    
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}
