package com.osb.pipeline.service;

import com.osb.pipeline.model.PipelineExecution;
import com.osb.pipeline.model.PipelineMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PipelineLoggingService {
    
    public void logPipelineStart(PipelineExecution execution, PipelineMessage message) {
        log.info("=== Pipeline Execution Started ===");
        log.info("Execution ID: {}", execution.getExecutionId());
        log.info("Pipeline: {}", execution.getPipelineName());
        log.info("Queue: {}", execution.getQueueName());
        log.info("Message ID: {}", message.getMessageId());
        log.info("Timestamp: {}", execution.getStartTime());
        log.debug("Message Payload: {}", message.getPayload());
    }
    
    public void logValidationStart(String executionId, String pipelineName) {
        log.info("[{}] Starting validation for pipeline: {}", executionId, pipelineName);
    }
    
    public void logValidationSuccess(String executionId) {
        log.info("[{}] Validation completed successfully", executionId);
    }
    
    public void logSchemaValidationStart(String executionId, String schemaPath) {
        log.info("[{}] Starting schema validation with: {}", executionId, schemaPath);
    }
    
    public void logSchemaValidationSuccess(String executionId) {
        log.info("[{}] Schema validation completed successfully", executionId);
    }
    
    public void logTransformationStart(String executionId, String xqueryPath) {
        log.info("[{}] Starting XQuery transformation with: {}", executionId, xqueryPath);
    }
    
    public void logTransformationSuccess(String executionId) {
        log.info("[{}] Transformation completed successfully", executionId);
    }
    
    public void logRoutingStart(String executionId, String routeType, String destination) {
        log.info("[{}] Starting routing to {} destination: {}", executionId, routeType, destination);
    }
    
    public void logRoutingSuccess(String executionId) {
        log.info("[{}] Routing completed successfully", executionId);
    }
    
    public void logPipelineComplete(PipelineExecution execution) {
        log.info("=== Pipeline Execution Completed ===");
        log.info("Execution ID: {}", execution.getExecutionId());
        log.info("Pipeline: {}", execution.getPipelineName());
        log.info("Status: {}", execution.getStatus());
        log.info("Duration: {} ms", 
            java.time.Duration.between(execution.getStartTime(), execution.getEndTime()).toMillis());
    }
    
    public void logPipelineError(PipelineExecution execution, Exception e) {
        log.error("=== Pipeline Execution Failed ===");
        log.error("Execution ID: {}", execution.getExecutionId());
        log.error("Pipeline: {}", execution.getPipelineName());
        log.error("Error Message: {}", e.getMessage());
        log.error("Stack Trace: ", e);
    }
}
