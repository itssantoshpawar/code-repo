package com.osb.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineMessage {
    
    private String messageId;
    private String queueName;
    private String payload;
    private Map<String, String> headers;
    private LocalDateTime timestamp;
    
    public String getHeader(String key) {
        return headers != null ? headers.get(key) : null;
    }
    
    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }
}
