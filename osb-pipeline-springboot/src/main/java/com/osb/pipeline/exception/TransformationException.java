package com.osb.pipeline.exception;

/**
 * Exception thrown when transformation fails
 */
public class TransformationException extends PipelineException {
    
    public TransformationException(String message) {
        super(message);
    }
    
    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}
