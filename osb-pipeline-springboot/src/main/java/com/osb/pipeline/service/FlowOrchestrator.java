package com.osb.pipeline.service;

import com.osb.pipeline.config.ConfigurationManager;
import com.osb.pipeline.core.PipelineProcessor;
import com.osb.pipeline.exception.PipelineException;
import com.osb.pipeline.model.FlowConfiguration;
import com.osb.pipeline.model.PipelineContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Orchestrates execution of multiple pipeline flows
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowOrchestrator {
    
    private final ConfigurationManager configurationManager;
    private final PipelineProcessor pipelineProcessor;
    
    private final Map<String, Map<String, Object>> flowResults = new HashMap<>();
    private final Map<String, String> flowErrors = new HashMap<>();
    
    /**
     * Register a flow from configuration file
     */
    public void registerFlowFromFile(String flowName, String configPath) {
        configurationManager.loadFromFile(configPath, flowName);
        log.info("Registered flow '{}' from {}", flowName, configPath);
    }
    
    /**
     * Register a flow from configuration object
     */
    public void registerFlow(String flowName, FlowConfiguration config) {
        configurationManager.registerConfiguration(flowName, config);
        log.info("Registered flow: {}", flowName);
    }
    
    /**
     * Execute a flow
     */
    public Map<String, Object> executeFlow(String flowName, Map<String, Object> inputData) {
        return executeFlow(flowName, inputData, new HashMap<>());
    }
    
    /**
     * Execute a flow with context
     */
    public Map<String, Object> executeFlow(String flowName, Map<String, Object> inputData, 
            Map<String, Object> contextMetadata) {
        
        log.info("Starting flow execution: {}", flowName);
        
        try {
            // Get flow configuration
            FlowConfiguration config = configurationManager.getConfiguration(flowName);
            
            // Set up context
            PipelineContext context = PipelineContext.builder()
                .flowName(flowName)
                .metadata(contextMetadata)
                .build();
            
            // Execute pipeline
            Map<String, Object> result = pipelineProcessor.processPipeline(
                new HashMap<>(inputData), 
                config.getStages(), 
                context
            );
            
            // Store result
            flowResults.put(flowName, Map.of(
                "result", result,
                "context", context
            ));
            
            log.info("Flow '{}' completed successfully", flowName);
            return result;
            
        } catch (PipelineException e) {
            log.error("Flow '{}' failed: {}", flowName, e.getMessage());
            flowErrors.put(flowName, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get flow result
     */
    public Map<String, Object> getFlowResult(String flowName) {
        return flowResults.get(flowName);
    }
    
    /**
     * Get flow error
     */
    public String getFlowError(String flowName) {
        return flowErrors.get(flowName);
    }
    
    /**
     * Clear all results
     */
    public void clearResults() {
        flowResults.clear();
        flowErrors.clear();
    }
}
