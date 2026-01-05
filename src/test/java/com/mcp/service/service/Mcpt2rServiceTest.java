package com.mcp.service.service;

import com.mcp.service.config.RoutingMapConfiguration;
import com.mcp.service.config.RoutingQueryConfiguration;
import com.mcp.service.model.McpRequest;
import com.mcp.service.model.McpResponse;
import com.mcp.service.model.RoutingDetails;
import com.mcp.service.util.TransformationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Mcpt2rServiceTest {
    
    @Mock
    private RoutingMapConfiguration routingMapConfiguration;
    
    @Mock
    private RoutingQueryConfiguration routingQueryConfiguration;
    
    @Mock
    private TransformationService transformationService;
    
    @InjectMocks
    private Mcpt2rService mcpt2rService;
    
    private McpRequest testRequest;
    private RoutingDetails testRoutingDetails;
    
    @BeforeEach
    void setUp() {
        testRequest = McpRequest.builder()
                .flowType("t2r")
                .domain("default")
                .payload("test payload")
                .correlationId("corr-123")
                .build();
        
        testRoutingDetails = RoutingDetails.builder()
                .flowType("t2r")
                .domain("default")
                .queueName("t2r-queue")
                .targetEndpoint("/api/t2r/process")
                .transformationType("standard")
                .requiresTransformation(true)
                .build();
    }
    
    @Test
    void testProcessT2rRequest_Success() {
        // Arrange
        when(routingMapConfiguration.getRoutingDetails("t2r")).thenReturn(testRoutingDetails);
        when(routingQueryConfiguration.getQuery("t2r")).thenReturn("SELECT * FROM routing_table");
        when(transformationService.transform(anyString(), anyString())).thenReturn("[T2R-TRANSFORMED]test payload");
        
        // Act
        McpResponse response = mcpt2rService.processT2rRequest(testRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("T2R request processed successfully", response.getMessage());
        assertEquals("corr-123", response.getCorrelationId());
        assertNotNull(response.getData());
        
        verify(routingMapConfiguration, times(1)).getRoutingDetails("t2r");
        verify(transformationService, times(1)).transform(anyString(), anyString());
    }
    
    @Test
    void testProcessT2rRequest_NoRoutingDetails() {
        // Arrange
        when(routingMapConfiguration.getRoutingDetails("t2r")).thenReturn(null);
        
        // Act
        McpResponse response = mcpt2rService.processT2rRequest(testRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("ERROR", response.getStatus());
        assertEquals("Routing configuration not found", response.getMessage());
        assertEquals("corr-123", response.getCorrelationId());
    }
    
    @Test
    void testProcessT2rRequest_Exception() {
        // Arrange
        when(routingMapConfiguration.getRoutingDetails("t2r")).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        McpResponse response = mcpt2rService.processT2rRequest(testRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("ERROR", response.getStatus());
        assertTrue(response.getMessage().contains("Test exception"));
    }
}
