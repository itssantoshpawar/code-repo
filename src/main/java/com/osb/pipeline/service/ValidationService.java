package com.osb.pipeline.service;

import com.osb.pipeline.model.PipelineMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidationService {
    
    public void validate(PipelineMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        
        if (message.getPayload() == null || message.getPayload().trim().isEmpty()) {
            throw new IllegalArgumentException("Message payload cannot be null or empty");
        }
        
        if (message.getQueueName() == null || message.getQueueName().trim().isEmpty()) {
            throw new IllegalArgumentException("Queue name cannot be null or empty");
        }
        
        // Additional validation checks can be added here
        log.debug("Basic validation passed for message: {}", message.getMessageId());
    }
}
