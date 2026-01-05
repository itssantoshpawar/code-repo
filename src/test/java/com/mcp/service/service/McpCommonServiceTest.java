package com.mcp.service.service;

import com.mcp.service.config.RoutingMapConfiguration;
import com.mcp.service.config.RoutingQueryConfiguration;
import com.mcp.service.model.McpRequest;
import com.mcp.service.model.McpResponse;
import com.mcp.service.model.RoutingDetails;
import com.mcp.service.util.FlowIdentifier;
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
class McpCommonServiceTest {
    
    @Mock
    private RoutingMapConfiguration routingMapConfiguration;
    
    @Mock
    private RoutingQueryConfiguration routingQueryConfiguration;
    
    @Mock
    private TransformationService transformationService;
    
    @Mock
    private FlowIdentifier flowIdentifier;
    
    @InjectMocks
    private McpCommonService mcpCommonService;
    
    private RoutingDetails testRoutingDetails;
    
    @BeforeEach
    void setUp() {
        testRoutingDetails = RoutingDetails.builder()
                .flowType("b2b")
                .domain("domain1")
                .queueName("b2b-domain1-queue")
                .targetEndpoint("/api/b2b/process")
                .transformationType("b2b")
                .requiresTransformation(true)
                .build();
    }
    
    @Test
    void testProcessRequest_B2bFlow_Success() {
        // Arrange
        McpRequest request = McpRequest.builder()
                .flowType("b2b")
                .domain("domain1")
                .payload("test payload")
                .correlationId("corr-123")
                .build();
        
        when(routingMapConfiguration.getRoutingDetails("b2b", "domain1")).thenReturn(testRoutingDetails);
        when(routingQueryConfiguration.getQuery("b2b")).thenReturn("SELECT * FROM routing_table");
        when(transformationService.transform(anyString(), anyString())).thenReturn("[B2B-TRANSFORMED]test payload");
        
        // Act
        McpResponse response = mcpCommonService.processRequest(request, "b2b-domain1-queue", "b2b");
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getMessage().contains("B2B"));
        assertEquals("corr-123", response.getCorrelationId());
        assertNotNull(response.getData());
    }
    
    @Test
    void testProcessRequest_SyncFlow_Success() {
        // Arrange
        McpRequest request = McpRequest.builder()
                .flowType("sync")
                .payload("test payload")
                .correlationId("corr-456")
                .build();
        
        RoutingDetails syncRoutingDetails = RoutingDetails.builder()
                .flowType("sync")
                .domain("default")
                .queueName(null)
                .targetEndpoint("/api/sync/process")
                .transformationType("sync")
                .requiresTransformation(false)
                .build();
        
        when(routingMapConfiguration.getRoutingDetails("sync", null)).thenReturn(syncRoutingDetails);
        when(routingQueryConfiguration.getQuery("sync")).thenReturn("SELECT * FROM routing_table");
        
        // Act
        McpResponse response = mcpCommonService.processRequest(request, null, "sync");
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Sync request processed successfully", response.getMessage());
        assertEquals("corr-456", response.getCorrelationId());
    }
    
    @Test
    void testProcessRequest_InvalidFlow() {
        // Arrange
        McpRequest request = McpRequest.builder()
                .flowType("invalid")
                .payload("test payload")
                .correlationId("corr-789")
                .build();
        
        // Act
        McpResponse response = mcpCommonService.processRequest(request, null, "invalid");
        
        // Assert
        assertNotNull(response);
        assertEquals("ERROR", response.getStatus());
        assertTrue(response.getMessage().contains("Invalid or unsupported flow type"));
    }
    
    @Test
    void testProcessRequest_FromQueueName() {
        // Arrange
        McpRequest request = McpRequest.builder()
                .payload("test payload")
                .correlationId("corr-999")
                .build();
        
        when(flowIdentifier.identifyFlowFromQueueName("siebel-domain1-queue")).thenReturn("siebel");
        when(flowIdentifier.extractDomainFromQueueName("siebel-domain1-queue")).thenReturn("domain1");
        
        RoutingDetails siebelRoutingDetails = RoutingDetails.builder()
                .flowType("siebel")
                .domain("domain1")
                .queueName("siebel-domain1-queue")
                .targetEndpoint("/api/siebel/process")
                .transformationType("siebel")
                .requiresTransformation(true)
                .build();
        
        when(routingMapConfiguration.getRoutingDetails("siebel", "domain1")).thenReturn(siebelRoutingDetails);
        when(routingQueryConfiguration.getQuery("siebel")).thenReturn("SELECT * FROM routing_table");
        when(transformationService.transform(anyString(), anyString())).thenReturn("[SIEBEL-TRANSFORMED]test payload");
        
        // Act
        McpResponse response = mcpCommonService.processRequest(request, "siebel-domain1-queue", null);
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getMessage().contains("SIEBEL"));
    }
    
    @Test
    void testProcessRequest_PortalFlow() {
        // Arrange
        McpRequest request = McpRequest.builder()
                .flowType("portal")
                .payload("test payload")
                .correlationId("corr-portal")
                .build();
        
        RoutingDetails portalRoutingDetails = RoutingDetails.builder()
                .flowType("portal")
                .domain("default")
                .queueName("portal-queue")
                .targetEndpoint("/api/portal/process")
                .transformationType("portal")
                .requiresTransformation(true)
                .build();
        
        when(routingMapConfiguration.getRoutingDetails("portal", null)).thenReturn(portalRoutingDetails);
        when(routingQueryConfiguration.getQuery("portal")).thenReturn("SELECT * FROM routing_table");
        when(transformationService.transform(anyString(), anyString())).thenReturn("[PORTAL-TRANSFORMED]test payload");
        
        // Act
        McpResponse response = mcpCommonService.processRequest(request, null, "portal");
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getMessage().contains("PORTAL"));
    }
    
    @Test
    void testProcessRequest_NeoFlow() {
        // Arrange
        McpRequest request = McpRequest.builder()
                .flowType("neo")
                .payload("test payload")
                .correlationId("corr-neo")
                .build();
        
        RoutingDetails neoRoutingDetails = RoutingDetails.builder()
                .flowType("neo")
                .domain("default")
                .queueName("neo-queue")
                .targetEndpoint("/api/neo/process")
                .transformationType("neo")
                .requiresTransformation(true)
                .build();
        
        when(routingMapConfiguration.getRoutingDetails("neo", null)).thenReturn(neoRoutingDetails);
        when(routingQueryConfiguration.getQuery("neo")).thenReturn("SELECT * FROM routing_table");
        when(transformationService.transform(anyString(), anyString())).thenReturn("[NEO-TRANSFORMED]test payload");
        
        // Act
        McpResponse response = mcpCommonService.processRequest(request, null, "neo");
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getMessage().contains("NEO"));
    }
}
