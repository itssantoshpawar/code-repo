package com.osb.pipeline.service;

import com.osb.pipeline.config.PipelineProperties;
import com.osb.pipeline.model.PipelineExecution;
import com.osb.pipeline.model.PipelineMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineOrchestrationService {
    
    private final PipelineProperties pipelineProperties;
    private final PipelineLoggingService loggingService;
    private final ValidationService validationService;
    private final SchemaValidationService schemaValidationService;
    private final XQueryTransformationService xqueryTransformationService;
    private final RoutingService routingService;
    
    public void processPipeline(PipelineMessage message) {
        // Find the pipeline configuration based on queue name
        Optional<PipelineProperties.PipelineConfig> configOpt = findPipelineConfig(message.getQueueName());
        
        if (configOpt.isEmpty()) {
            log.error("No pipeline configuration found for queue: {}", message.getQueueName());
            return;
        }
        
        PipelineProperties.PipelineConfig config = configOpt.get();
        
        // Create execution tracking
        PipelineExecution execution = PipelineExecution.builder()
            .executionId(UUID.randomUUID().toString())
            .pipelineName(config.getName())
            .queueName(config.getQueueName())
            .messageId(message.getMessageId())
            .status(PipelineExecution.ExecutionStatus.STARTED)
            .startTime(LocalDateTime.now())
            .build();
        
        try {
            // Log pipeline start
            loggingService.logPipelineStart(execution, message);
            
            // Step 1: Validation
            if (config.isValidationEnabled()) {
                execution.setStatus(PipelineExecution.ExecutionStatus.VALIDATING);
                loggingService.logValidationStart(execution.getExecutionId(), config.getName());
                validationService.validate(message);
                loggingService.logValidationSuccess(execution.getExecutionId());
            }
            
            // Step 2: Schema Validation
            if (config.isSchemaValidationEnabled() && config.getSchemaPath() != null) {
                loggingService.logSchemaValidationStart(execution.getExecutionId(), config.getSchemaPath());
                schemaValidationService.validateAgainstSchema(message.getPayload(), config.getSchemaPath());
                loggingService.logSchemaValidationSuccess(execution.getExecutionId());
            }
            
            // Step 3: XQuery Transformation
            String payloadToRoute = message.getPayload();
            if (config.isXqueryEnabled() && config.getXqueryPath() != null) {
                execution.setStatus(PipelineExecution.ExecutionStatus.TRANSFORMING);
                loggingService.logTransformationStart(execution.getExecutionId(), config.getXqueryPath());
                payloadToRoute = xqueryTransformationService.transform(message.getPayload(), config.getXqueryPath());
                loggingService.logTransformationSuccess(execution.getExecutionId());
            }
            
            // Step 4: Routing
            execution.setStatus(PipelineExecution.ExecutionStatus.ROUTING);
            loggingService.logRoutingStart(execution.getExecutionId(), 
                config.getRouteType().name(), config.getRouteDestination());
            routingService.route(payloadToRoute, config.getRouteType(), config.getRouteDestination());
            loggingService.logRoutingSuccess(execution.getExecutionId());
            
            // Complete
            execution.setStatus(PipelineExecution.ExecutionStatus.COMPLETED);
            execution.setEndTime(LocalDateTime.now());
            loggingService.logPipelineComplete(execution);
            
        } catch (Exception e) {
            execution.setStatus(PipelineExecution.ExecutionStatus.FAILED);
            execution.setEndTime(LocalDateTime.now());
            execution.setErrorMessage(e.getMessage());
            loggingService.logPipelineError(execution, e);
            throw new RuntimeException("Pipeline execution failed", e);
        }
    }
    
    private Optional<PipelineProperties.PipelineConfig> findPipelineConfig(String queueName) {
        return pipelineProperties.getPipelines().stream()
            .filter(config -> config.getQueueName().equals(queueName))
            .findFirst();
    }
}
