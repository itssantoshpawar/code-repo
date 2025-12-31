package com.osb.pipeline.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.osb.pipeline.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates data against JSON schemas
 */
@Slf4j
@Component
public class SchemaValidator {
    
    private final Map<String, JsonSchema> schemas = new HashMap<>();
    private final JsonSchemaFactory schemaFactory = 
        JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Register a schema for validation
     */
    public void registerSchema(String schemaName, Map<String, Object> schemaDefinition) {
        try {
            JsonNode schemaNode = objectMapper.valueToTree(schemaDefinition);
            JsonSchema schema = schemaFactory.getSchema(schemaNode);
            schemas.put(schemaName, schema);
            log.info("Registered schema: {}", schemaName);
        } catch (Exception e) {
            throw new ValidationException("Error registering schema: " + e.getMessage(), e);
        }
    }
    
    /**
     * Load schema from file
     */
    public void loadSchemaFromFile(String schemaPath, String schemaName) {
        try {
            JsonNode schemaNode = objectMapper.readTree(new File(schemaPath));
            JsonSchema schema = schemaFactory.getSchema(schemaNode);
            schemas.put(schemaName, schema);
            log.info("Loaded schema from file: {}", schemaPath);
        } catch (IOException e) {
            throw new ValidationException("Error loading schema: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate data against a registered schema
     */
    public void validate(Map<String, Object> data, String schemaName) {
        JsonSchema schema = schemas.get(schemaName);
        if (schema == null) {
            throw new ValidationException("Schema not found: " + schemaName);
        }
        
        validateWithSchema(data, schema);
    }
    
    /**
     * Validate data against an inline schema
     */
    public void validateWithInlineSchema(Map<String, Object> data, Map<String, Object> schemaDefinition) {
        try {
            JsonNode schemaNode = objectMapper.valueToTree(schemaDefinition);
            JsonSchema schema = schemaFactory.getSchema(schemaNode);
            validateWithSchema(data, schema);
        } catch (Exception e) {
            throw new ValidationException("Error with inline schema: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate data with a schema
     */
    private void validateWithSchema(Map<String, Object> data, JsonSchema schema) {
        JsonNode dataNode = objectMapper.valueToTree(data);
        Set<ValidationMessage> errors = schema.validate(dataNode);
        
        if (!errors.isEmpty()) {
            String errorMessages = errors.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.joining(", "));
            throw new ValidationException("Validation failed: " + errorMessages);
        }
        
        log.debug("Validation passed");
    }
}
