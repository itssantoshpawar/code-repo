package com.osb.pipeline.exception;

/**
 * Exception thrown when configuration is invalid
 */
public class ConfigurationException extends PipelineException {
    
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
