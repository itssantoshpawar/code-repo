package com.osb.pipeline.controller;

import com.osb.pipeline.factory.FlowHandlerFactory;
import com.osb.pipeline.handler.Flow1Handler;
import com.osb.pipeline.model.FlowRequest;
import com.osb.pipeline.model.FlowResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlowController.class)
class FlowControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private FlowHandlerFactory flowHandlerFactory;
    
    @MockBean
    private Flow1Handler flow1Handler;
    
    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/flow/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
    
    @Test
    void testProcessFlowSuccess() throws Exception {
        when(flowHandlerFactory.getHandler("flow1-queue")).thenReturn(flow1Handler);
        
        FlowResponse mockResponse = FlowResponse.builder()
            .status("SUCCESS")
            .message("Processed successfully")
            .transformedPayload("transformed")
            .requestId("test-123")
            .build();
        
        when(flow1Handler.process(any(FlowRequest.class))).thenReturn(mockResponse);
        
        mockMvc.perform(post("/api/flow/process")
                .header("x-queue-name", "flow1-queue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("test payload"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
    
    @Test
    void testProcessFlowMissingHeader() throws Exception {
        mockMvc.perform(post("/api/flow/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content("test payload"))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void testProcessFlowValidationError() throws Exception {
        when(flowHandlerFactory.getHandler("flow1-queue")).thenReturn(flow1Handler);
        when(flow1Handler.process(any(FlowRequest.class)))
            .thenThrow(new IllegalArgumentException("Validation failed"));
        
        mockMvc.perform(post("/api/flow/process")
                .header("x-queue-name", "flow1-queue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("test payload"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("ERROR"));
    }
    
    @Test
    void testProcessFlowInvalidQueue() throws Exception {
        when(flowHandlerFactory.getHandler(anyString()))
            .thenThrow(new IllegalArgumentException("No handler configured"));
        
        mockMvc.perform(post("/api/flow/process")
                .header("x-queue-name", "invalid-queue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("test payload"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("ERROR"));
    }
}
