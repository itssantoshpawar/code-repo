package com.osb.pipeline.exception;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends PipelineException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
