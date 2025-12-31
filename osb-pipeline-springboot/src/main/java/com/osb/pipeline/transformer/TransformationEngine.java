package com.osb.pipeline.transformer;

import com.osb.pipeline.exception.TransformationException;
import com.osb.pipeline.model.TransformationConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Executes transformations on pipeline data
 */
@Slf4j
@Component
public class TransformationEngine {
    
    private final Map<String, BiFunction<Map<String, Object>, Map<String, Object>, Map<String, Object>>> 
        transformations = new HashMap<>();
    
    public TransformationEngine() {
        registerBuiltInTransformations();
    }
    
    /**
     * Register a custom transformation
     */
    public void registerTransformation(String name, 
            BiFunction<Map<String, Object>, Map<String, Object>, Map<String, Object>> transformer) {
        transformations.put(name, transformer);
        log.info("Registered transformation: {}", name);
    }
    
    /**
     * Apply a single transformation
     */
    public Map<String, Object> transform(Map<String, Object> data, TransformationConfiguration config) {
        String type = config.getType();
        
        if (!transformations.containsKey(type)) {
            throw new TransformationException("Unknown transformation type: " + type);
        }
        
        try {
            Map<String, Object> params = config.getParams() != null ? config.getParams() : new HashMap<>();
            return transformations.get(type).apply(new HashMap<>(data), params);
        } catch (Exception e) {
            throw new TransformationException("Transformation '" + type + "' failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Apply multiple transformations sequentially
     */
    public Map<String, Object> applyTransformations(Map<String, Object> data, 
            List<TransformationConfiguration> transformationConfigs) {
        Map<String, Object> result = data;
        for (TransformationConfiguration config : transformationConfigs) {
            result = transform(result, config);
        }
        return result;
    }
    
    /**
     * Register built-in transformations
     */
    private void registerBuiltInTransformations() {
        transformations.put("map_fields", this::mapFields);
        transformations.put("rename_field", this::renameField);
        transformations.put("add_field", this::addField);
        transformations.put("remove_field", this::removeField);
        transformations.put("transform_value", this::transformValue);
        transformations.put("concat_fields", this::concatFields);
        transformations.put("split_field", this::splitField);
        transformations.put("uppercase", this::uppercase);
        transformations.put("lowercase", this::lowercase);
        transformations.put("replace", this::replace);
    }
    
    // Built-in transformation implementations
    
    private Map<String, Object> mapFields(Map<String, Object> data, Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        Map<String, String> mapping = (Map<String, String>) params.get("mapping");
        boolean includeUnmapped = Boolean.TRUE.equals(params.get("include_unmapped"));
        
        Map<String, Object> result = new HashMap<>();
        
        if (mapping != null) {
            mapping.forEach((sourceField, targetField) -> {
                if (data.containsKey(sourceField)) {
                    result.put(targetField, data.get(sourceField));
                }
            });
        }
        
        if (includeUnmapped && mapping != null) {
            data.forEach((key, value) -> {
                if (!mapping.containsKey(key) && !result.containsKey(key)) {
                    result.put(key, value);
                }
            });
        }
        
        return result;
    }
    
    private Map<String, Object> renameField(Map<String, Object> data, Map<String, Object> params) {
        String oldName = (String) params.get("old_name");
        String newName = (String) params.get("new_name");
        
        if (oldName != null && newName != null && data.containsKey(oldName)) {
            data.put(newName, data.remove(oldName));
        }
        
        return data;
    }
    
    private Map<String, Object> addField(Map<String, Object> data, Map<String, Object> params) {
        String fieldName = (String) params.get("name");
        Object fieldValue = params.get("value");
        
        if (fieldName != null) {
            data.put(fieldName, fieldValue);
        }
        
        return data;
    }
    
    private Map<String, Object> removeField(Map<String, Object> data, Map<String, Object> params) {
        String fieldName = (String) params.get("name");
        
        if (fieldName != null) {
            data.remove(fieldName);
        }
        
        return data;
    }
    
    private Map<String, Object> transformValue(Map<String, Object> data, Map<String, Object> params) {
        String fieldName = (String) params.get("field");
        String operation = (String) params.get("operation");
        
        if (fieldName != null && data.containsKey(fieldName)) {
            Object value = data.get(fieldName);
            
            if ("multiply".equals(operation) && value instanceof Number) {
                Number factor = params.get("factor") != null ? (Number) params.get("factor") : 1;
                data.put(fieldName, ((Number) value).doubleValue() * factor.doubleValue());
            } else if ("add".equals(operation) && value instanceof Number) {
                Number addValue = params.get("value") != null ? (Number) params.get("value") : 0;
                data.put(fieldName, ((Number) value).doubleValue() + addValue.doubleValue());
            } else if ("format".equals(operation)) {
                String template = (String) params.getOrDefault("template", "{}");
                data.put(fieldName, template.replace("{}", String.valueOf(value)));
            }
        }
        
        return data;
    }
    
    private Map<String, Object> concatFields(Map<String, Object> data, Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<String> fields = (List<String>) params.get("fields");
        String target = (String) params.get("target");
        String separator = (String) params.getOrDefault("separator", "");
        
        if (target != null && fields != null) {
            StringJoiner joiner = new StringJoiner(separator);
            fields.forEach(field -> {
                if (data.containsKey(field)) {
                    joiner.add(String.valueOf(data.get(field)));
                }
            });
            data.put(target, joiner.toString());
        }
        
        return data;
    }
    
    private Map<String, Object> splitField(Map<String, Object> data, Map<String, Object> params) {
        String fieldName = (String) params.get("field");
        String separator = (String) params.getOrDefault("separator", ",");
        @SuppressWarnings("unchecked")
        List<String> targets = (List<String>) params.get("targets");
        
        if (fieldName != null && data.containsKey(fieldName) && targets != null) {
            String value = String.valueOf(data.get(fieldName));
            String[] parts = value.split(separator);
            
            for (int i = 0; i < Math.min(parts.length, targets.size()); i++) {
                data.put(targets.get(i), parts[i].trim());
            }
        }
        
        return data;
    }
    
    private Map<String, Object> uppercase(Map<String, Object> data, Map<String, Object> params) {
        String fieldName = (String) params.get("field");
        
        if (fieldName != null && data.containsKey(fieldName)) {
            Object value = data.get(fieldName);
            if (value instanceof String) {
                data.put(fieldName, ((String) value).toUpperCase());
            }
        }
        
        return data;
    }
    
    private Map<String, Object> lowercase(Map<String, Object> data, Map<String, Object> params) {
        String fieldName = (String) params.get("field");
        
        if (fieldName != null && data.containsKey(fieldName)) {
            Object value = data.get(fieldName);
            if (value instanceof String) {
                data.put(fieldName, ((String) value).toLowerCase());
            }
        }
        
        return data;
    }
    
    private Map<String, Object> replace(Map<String, Object> data, Map<String, Object> params) {
        String fieldName = (String) params.get("field");
        String pattern = (String) params.get("pattern");
        String replacement = (String) params.getOrDefault("replacement", "");
        
        if (fieldName != null && data.containsKey(fieldName) && pattern != null) {
            Object value = data.get(fieldName);
            if (value instanceof String) {
                data.put(fieldName, ((String) value).replace(pattern, replacement));
            }
        }
        
        return data;
    }
}
