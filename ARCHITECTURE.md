# OSB Pipeline Flows Architecture

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         REST Client                              │
│                    (External Systems)                            │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ HTTP POST
                             │ Header: x-queue-name
                             │ Body: payload
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      FlowController                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ • Validates queue name format                             │  │
│  │ • Generates request ID                                    │  │
│  │ • Delegates to FlowHandlerFactory                         │  │
│  │ • Returns FlowResponse                                    │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   FlowHandlerFactory                             │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ • Maps queue name to handler                              │  │
│  │ • Auto-registers all @Component handlers                  │  │
│  │ • Returns appropriate FlowHandler                         │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
            ▼                ▼                ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────────┐
│  Flow1Handler    │ │ Flow2Handler │ │  Flow3Handler    │
│  (flow1-queue)   │ │(flow2-queue) │ │  (flow3-queue)   │
└──────────────────┘ └──────────────┘ └──────────────────┘
            │                │                │
            ▼                ▼                ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────────┐
│  Flow4Handler    │ │ Flow5Handler │ │  Flow6Handler    │
│  (flow4-queue)   │ │(flow5-queue) │ │  (flow6-queue)   │
└──────────────────┘ └──────────────┘ └──────────────────┘
            │                │                │
            └────────────────┼────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    AbstractFlowHandler                           │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ • Common validation (null, empty, requestId)              │  │
│  │ • Orchestrates: validate → process → transform → route   │  │
│  │ • Provides logging infrastructure                         │  │
│  │ • Delegates to:                                           │  │
│  │   - processFlowSpecific() (in subclass)                   │  │
│  │   - validateFlowSpecific() (in subclass)                  │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────┬────────────────────────┬─────────────────────┘
                   │                        │
                   ▼                        ▼
┌──────────────────────────────┐  ┌────────────────────────────┐
│     TransformationUtil       │  │      RoutingUtil           │
│  ┌────────────────────────┐  │  │  ┌──────────────────────┐  │
│  │ • Transform payload    │  │  │  │ • Route to queues    │  │
│  │ • Add metadata wrapper │  │  │  │ • Priority routing   │  │
│  │ • JSON escaping        │  │  │  │ • Error routing      │  │
│  └────────────────────────┘  │  │  └──────────────────────┘  │
└──────────────────────────────┘  └────────────────────────────┘
```

## Flow Processing Sequence

```
1. Request arrives at FlowController
   ├─ Validate queue name format (alphanumeric, hyphens, underscores)
   └─ Generate request ID

2. FlowHandlerFactory selects handler
   ├─ Lookup handler by queue name
   └─ Return appropriate FlowHandler

3. Handler.process() called
   ├─ Common validation (AbstractFlowHandler)
   │  ├─ Check null request
   │  ├─ Check null/empty payload
   │  └─ Check null/empty requestId
   │
   ├─ Flow-specific validation (subclass)
   │  └─ Apply flow-specific rules
   │
   ├─ Flow-specific processing (subclass)
   │  └─ Apply flow-specific transformations
   │
   ├─ Common transformation (TransformationUtil)
   │  ├─ Add request ID metadata
   │  └─ Escape JSON special characters
   │
   └─ Common routing (RoutingUtil)
      ├─ Determine destination queue
      └─ Validate routing path

4. Return FlowResponse
   ├─ Status: SUCCESS/ERROR
   ├─ Message: Description
   ├─ Transformed payload
   └─ Request ID
```

## Flow-Specific Behaviors

| Flow | Queue Name    | Validation | Processing |
|------|--------------|------------|------------|
| 1    | flow1-queue  | Min 10 chars | Normalize whitespace |
| 2    | flow2-queue  | Must have letters | Convert to uppercase |
| 3    | flow3-queue  | Max 1000 chars | Add "[FLOW3]" prefix |
| 4    | flow4-queue  | Headers required | Reverse string |
| 5    | flow5-queue  | Must contain "data" | Add "[FLOW5]" suffix |
| 6    | flow6-queue  | 5-500 chars | Replace spaces, lowercase |

## Data Flow

```
Input Request
    │
    ├─ x-queue-name: flow1-queue
    ├─ payload: "Test Message"
    └─ headers: {...}
    │
    ▼
FlowRequest Object
    │
    ├─ queueName: "flow1-queue"
    ├─ payload: "Test Message"
    ├─ headers: {...}
    └─ requestId: "uuid-123"
    │
    ▼
Flow Processing
    │
    ├─ Validation ✓
    ├─ Processing: "Test Message" → "Test Message"
    └─ Transformation: → {"requestId":"uuid-123","data":"Test Message"}
    │
    ▼
FlowResponse Object
    │
    ├─ status: "SUCCESS"
    ├─ message: "Processed successfully for flow1-queue"
    ├─ transformedPayload: "{...}"
    └─ requestId: "uuid-123"
```

## Extension Points

### Adding a New Flow

1. Create new handler class:
```java
@Component
public class Flow7Handler extends AbstractFlowHandler {
    @Override
    public String getQueueName() {
        return "flow7-queue";
    }
    
    @Override
    protected String processFlowSpecific(FlowRequest request) {
        // Your logic here
    }
    
    @Override
    protected void validateFlowSpecific(FlowRequest request) {
        // Your validation here
    }
}
```

2. Spring automatically registers it in FlowHandlerFactory
3. No changes needed to existing code
4. Start using with x-queue-name: flow7-queue

### Customizing Utilities

- **RoutingUtil**: Modify `routeMessage()` for new routing rules
- **TransformationUtil**: Modify `transformPayload()` for new transformations
- Both affect all flows simultaneously

## Security Layers

```
┌─────────────────────────────────────────────┐
│ Layer 1: Input Validation                   │
│ • Queue name format check                   │
│ • Alphanumeric + hyphens + underscores only │
│ • Max 100 characters                        │
└─────────────────────────────────────────────┘
                    ▼
┌─────────────────────────────────────────────┐
│ Layer 2: Request Validation                 │
│ • Null checks on request                    │
│ • Null/empty checks on payload              │
│ • Request ID validation                     │
└─────────────────────────────────────────────┘
                    ▼
┌─────────────────────────────────────────────┐
│ Layer 3: Flow-Specific Validation           │
│ • Size constraints                          │
│ • Content requirements                      │
│ • Header requirements                       │
└─────────────────────────────────────────────┘
                    ▼
┌─────────────────────────────────────────────┐
│ Layer 4: Output Sanitization                │
│ • JSON escaping (\", \\, \n, \r, \t)       │
│ • Safe string concatenation                 │
│ • Structured logging                        │
└─────────────────────────────────────────────┘
```

## Testing Strategy

```
Unit Tests
    ├─ Flow1HandlerTest
    │  ├─ Test validation rules
    │  ├─ Test processing logic
    │  └─ Test error handling
    │
    └─ FlowHandlerFactoryTest
       ├─ Test handler registration
       ├─ Test handler lookup
       └─ Test error cases

Integration Tests
    └─ FlowControllerTest
       ├─ Test all endpoints
       ├─ Test error handling
       ├─ Test missing headers
       └─ Test validation errors

Manual Tests
    ├─ All 6 flows tested
    ├─ Special characters tested
    ├─ Invalid inputs tested
    └─ Security scenarios tested
```
