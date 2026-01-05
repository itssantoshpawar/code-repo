package com.osb.pipeline.handler;

import com.osb.pipeline.model.FlowRequest;
import com.osb.pipeline.model.FlowResponse;
import com.osb.pipeline.util.RoutingUtil;
import com.osb.pipeline.util.TransformationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Flow1HandlerTest {
    
    @Mock
    private RoutingUtil routingUtil;
    
    @Mock
    private TransformationUtil transformationUtil;
    
    @InjectMocks
    private Flow1Handler flow1Handler;
    
    @BeforeEach
    void setUp() {
        lenient().when(routingUtil.routeMessage(anyString())).thenReturn("standard-queue");
        lenient().when(transformationUtil.transformPayload(anyString(), anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }
    
    @Test
    void testGetQueueName() {
        assertEquals("flow1-queue", flow1Handler.getQueueName());
    }
    
    @Test
    void testProcessSuccess() {
        FlowRequest request = FlowRequest.builder()
            .queueName("flow1-queue")
            .payload("This is a test payload")
            .headers(new HashMap<>())
            .requestId("test-123")
            .build();
        
        FlowResponse response = flow1Handler.process(request);
        
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("test-123", response.getRequestId());
    }
    
    @Test
    void testValidateSuccess() {
        FlowRequest request = FlowRequest.builder()
            .payload("Valid payload with more than 10 characters")
            .requestId("test-123")
            .build();
        
        assertDoesNotThrow(() -> flow1Handler.validate(request));
    }
    
    @Test
    void testValidateFailureTooShort() {
        FlowRequest request = FlowRequest.builder()
            .payload("short")
            .requestId("test-123")
            .build();
        
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> flow1Handler.validate(request));
        
        assertTrue(exception.getMessage().contains("at least 10 characters"));
    }
    
    @Test
    void testValidateNullPayload() {
        FlowRequest request = FlowRequest.builder()
            .payload(null)
            .requestId("test-123")
            .build();
        
        assertThrows(IllegalArgumentException.class, 
            () -> flow1Handler.validate(request));
    }
}
