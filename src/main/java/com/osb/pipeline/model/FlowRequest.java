package com.osb.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request model for flow processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowRequest {
    private String queueName;
    private String payload;
    private Map<String, String> headers;
    private String requestId;
}
