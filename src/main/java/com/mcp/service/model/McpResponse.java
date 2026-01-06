package com.mcp.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpResponse {
    private String status;
    private String message;
    private String correlationId;
    private Object data;
}
