package com.osb.pipeline.service;

import com.osb.pipeline.config.PipelineProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutingService {
    
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    
    public void route(String payload, PipelineProperties.RouteType routeType, String destination) {
        switch (routeType) {
            case SERVICE:
                routeToService(payload, destination);
                break;
            case QUEUE:
                routeToQueue(payload, destination);
                break;
            default:
                throw new IllegalArgumentException("Unknown route type: " + routeType);
        }
    }
    
    private void routeToService(String payload, String serviceUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            
            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(serviceUrl, request, String.class);
            
            log.debug("Successfully routed to service: {}, Status: {}", 
                serviceUrl, response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Failed to route to service: " + serviceUrl, e);
        }
    }
    
    private void routeToQueue(String payload, String queueName) {
        try {
            rabbitTemplate.convertAndSend(queueName, payload);
            log.debug("Successfully routed to queue: {}", queueName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to route to queue: " + queueName, e);
        }
    }
}
