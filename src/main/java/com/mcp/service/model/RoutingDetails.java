package com.mcp.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutingDetails {
    private String flowType;
    private String domain;
    private String queueName;
    private String targetEndpoint;
    private String transformationType;
    private boolean requiresTransformation;
}
