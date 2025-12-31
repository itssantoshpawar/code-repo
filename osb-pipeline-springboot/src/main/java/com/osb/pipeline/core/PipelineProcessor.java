package com.osb.pipeline.core;

import com.osb.pipeline.exception.PipelineException;
import com.osb.pipeline.model.PipelineContext;
import com.osb.pipeline.model.StageConfiguration;
import com.osb.pipeline.transformer.TransformationEngine;
import com.osb.pipeline.validator.SchemaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes data through pipeline stages
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineProcessor {
    
    private final SchemaValidator schemaValidator;
    private final TransformationEngine transformationEngine;
    
    /**
     * Process a single stage
     */
    public Map<String, Object> processStage(Map<String, Object> data, 
            StageConfiguration stage, PipelineContext context) {
        
        String stageName = stage.getName();
        String stageType = stage.getType();
        
        log.info("Processing stage: {} (type: {})", stageName, stageType);
        
        try {
            Map<String, Object> result = switch (stageType.toLowerCase()) {
                case "validation" -> handleValidationStage(data, stage);
                case "transformation" -> handleTransformationStage(data, stage);
                case "enrichment" -> handleEnrichmentStage(data, stage);
                case "routing" -> handleRoutingStage(data, stage, context);
                default -> throw new PipelineException("Unknown stage type: " + stageType);
            };
            
            log.info("Stage '{}' completed successfully", stageName);
            return result;
            
        } catch (Exception e) {
            log.error("Stage '{}' failed: {}", stageName, e.getMessage());
            throw new PipelineException("Stage '" + stageName + "' failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Process data through multiple stages
     */
    public Map<String, Object> processPipeline(Map<String, Object> data, 
            List<StageConfiguration> stages, PipelineContext context) {
        
        Map<String, Object> result = data;
        context.setExecutionStart(LocalDateTime.now());
        
        for (StageConfiguration stage : stages) {
            String stageName = stage.getName();
            try {
                result = processStage(result, stage, context);
                context.getStagesCompleted().add(stageName);
            } catch (PipelineException e) {
                context.setFailedStage(stageName);
                throw e;
            }
        }
        
        context.setExecutionEnd(LocalDateTime.now());
        context.setStatus("success");
        return result;
    }
    
    // Stage handlers
    
    private Map<String, Object> handleValidationStage(Map<String, Object> data, 
            StageConfiguration stage) {
        
        if (stage.getSchema() != null) {
            schemaValidator.validate(data, stage.getSchema());
        } else if (stage.getInlineSchema() != null) {
            schemaValidator.validateWithInlineSchema(data, stage.getInlineSchema());
        } else {
            throw new PipelineException("Validation stage requires 'schema' or 'inlineSchema'");
        }
        
        return data;
    }
    
    private Map<String, Object> handleTransformationStage(Map<String, Object> data, 
            StageConfiguration stage) {
        
        if (stage.getTransformations() == null || stage.getTransformations().isEmpty()) {
            throw new PipelineException("Transformation stage requires 'transformations' list");
        }
        
        return transformationEngine.applyTransformations(data, stage.getTransformations());
    }
    
    private Map<String, Object> handleEnrichmentStage(Map<String, Object> data, 
            StageConfiguration stage) {
        
        Map<String, Object> enrichmentData = stage.getEnrichmentData();
        String mergeStrategy = stage.getMergeStrategy() != null ? stage.getMergeStrategy() : "update";
        
        if (enrichmentData != null) {
            if ("update".equals(mergeStrategy)) {
                data.putAll(enrichmentData);
            } else if ("append".equals(mergeStrategy)) {
                enrichmentData.forEach(data::putIfAbsent);
            }
        }
        
        return data;
    }
    
    private Map<String, Object> handleRoutingStage(Map<String, Object> data, 
            StageConfiguration stage, PipelineContext context) {
        
        String routingKey = stage.getRoutingKey();
        
        if (routingKey != null && data.containsKey(routingKey)) {
            context.setRoutingDecision(String.valueOf(data.get(routingKey)));
        }
        
        return data;
    }
}
