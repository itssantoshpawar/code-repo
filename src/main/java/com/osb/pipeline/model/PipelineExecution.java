package com.osb.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineExecution {
    
    private String executionId;
    private String pipelineName;
    private String queueName;
    private String messageId;
    private ExecutionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorMessage;
    
    public enum ExecutionStatus {
        STARTED,
        VALIDATING,
        TRANSFORMING,
        ROUTING,
        COMPLETED,
        FAILED
    }
}
