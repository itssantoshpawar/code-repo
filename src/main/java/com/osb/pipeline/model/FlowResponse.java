package com.osb.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model for flow processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowResponse {
    private String status;
    private String message;
    private String transformedPayload;
    private String requestId;
}
