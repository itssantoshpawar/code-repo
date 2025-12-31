package com.osb.pipeline.exception;

/**
 * Base exception for pipeline processing errors
 */
public class PipelineException extends RuntimeException {
    
    public PipelineException(String message) {
        super(message);
    }
    
    public PipelineException(String message, Throwable cause) {
        super(message, cause);
    }
}
