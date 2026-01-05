package com.osb.pipeline.listener;

import com.osb.pipeline.model.PipelineMessage;
import com.osb.pipeline.service.PipelineOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineMessageListener {
    
    private final PipelineOrchestrationService orchestrationService;
    
    @RabbitListener(queues = {
        "order-processing-queue",
        "payment-processing-queue",
        "customer-notification-queue",
        "inventory-update-queue",
        "audit-logging-queue",
        "error-handling-queue"
    })
    public void handleMessage(Message message) {
        try {
            // Extract queue name from x-queue-name header or use the actual queue
            String queueName = extractQueueName(message);
            String payload = new String(message.getBody());
            
            // Build PipelineMessage
            PipelineMessage pipelineMessage = PipelineMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .queueName(queueName)
                .payload(payload)
                .headers(extractHeaders(message))
                .timestamp(LocalDateTime.now())
                .build();
            
            log.info("Received message from queue: {}", queueName);
            
            // Process through pipeline orchestration
            orchestrationService.processPipeline(pipelineMessage);
            
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            // In production, you might want to send this to a DLQ (Dead Letter Queue)
        }
    }
    
    private String extractQueueName(Message message) {
        // First, check for x-queue-name header
        Object queueNameHeader = message.getMessageProperties().getHeader("x-queue-name");
        if (queueNameHeader != null) {
            return queueNameHeader.toString();
        }
        
        // Fallback to the actual consumer queue
        String consumerQueue = message.getMessageProperties().getConsumerQueue();
        if (consumerQueue != null) {
            return consumerQueue;
        }
        
        // Last resort - use received routing key
        return message.getMessageProperties().getReceivedRoutingKey();
    }
    
    private Map<String, String> extractHeaders(Message message) {
        Map<String, String> headers = new HashMap<>();
        message.getMessageProperties().getHeaders().forEach((key, value) -> {
            if (value != null) {
                headers.put(key, value.toString());
            }
        });
        return headers;
    }
}
