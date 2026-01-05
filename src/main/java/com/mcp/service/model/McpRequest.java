package com.mcp.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpRequest {
    private String flowType;
    private String domain;
    private String payload;
    private String correlationId;
}
