package com.osb.pipeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.osb.pipeline.exception.ConfigurationException;
import com.osb.pipeline.model.FlowConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages pipeline configurations from JSON or YAML files
 */
@Slf4j
@Component
public class ConfigurationManager {
    
    private final Map<String, FlowConfiguration> configurations = new HashMap<>();
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    /**
     * Load configuration from a file
     */
    public FlowConfiguration loadFromFile(String configPath, String flowName) {
        Path path = Path.of(configPath);
        File file = path.toFile();
        
        if (!file.exists()) {
            throw new ConfigurationException("Configuration file not found: " + configPath);
        }
        
        try {
            FlowConfiguration config;
            String fileName = file.getName().toLowerCase();
            
            if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
                config = yamlMapper.readValue(file, FlowConfiguration.class);
            } else if (fileName.endsWith(".json")) {
                config = jsonMapper.readValue(file, FlowConfiguration.class);
            } else {
                throw new ConfigurationException("Unsupported file format: " + fileName);
            }
            
            if (flowName != null) {
                configurations.put(flowName, config);
            }
            
            validateConfiguration(config);
            log.info("Loaded configuration for flow: {}", flowName);
            return config;
            
        } catch (IOException e) {
            throw new ConfigurationException("Error loading configuration: " + e.getMessage(), e);
        }
    }
    
    /**
     * Load configuration from object
     */
    public void registerConfiguration(String flowName, FlowConfiguration config) {
        validateConfiguration(config);
        configurations.put(flowName, config);
        log.info("Registered configuration for flow: {}", flowName);
    }
    
    /**
     * Get configuration for a flow
     */
    public FlowConfiguration getConfiguration(String flowName) {
        FlowConfiguration config = configurations.get(flowName);
        if (config == null) {
            throw new ConfigurationException("Configuration not found for flow: " + flowName);
        }
        return config;
    }
    
    /**
     * Get all registered flow names
     */
    public List<String> getAllFlowNames() {
        return List.copyOf(configurations.keySet());
    }
    
    /**
     * Validate configuration structure
     */
    private void validateConfiguration(FlowConfiguration config) {
        if (config.getFlowName() == null || config.getFlowName().isEmpty()) {
            throw new ConfigurationException("Flow name is required");
        }
        
        if (config.getStages() == null || config.getStages().isEmpty()) {
            throw new ConfigurationException("At least one stage is required");
        }
        
        for (int i = 0; i < config.getStages().size(); i++) {
            var stage = config.getStages().get(i);
            if (stage.getName() == null || stage.getName().isEmpty()) {
                throw new ConfigurationException("Stage " + i + " missing name");
            }
            if (stage.getType() == null || stage.getType().isEmpty()) {
                throw new ConfigurationException("Stage " + i + " missing type");
            }
        }
    }
}
