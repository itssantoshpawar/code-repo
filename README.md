# OSB Pipeline Flows - Spring Boot Application

This Spring Boot application provides a generic implementation for handling 6 Oracle Service Bus (OSB) pipeline flows. The design emphasizes simplicity, maintainability, and extensibility while preserving the behavior and steps from the OSB pipelines.

## Architecture Overview

### Core Components

1. **FlowHandler Interface** (`com.osb.pipeline.handler.FlowHandler`)
   - Defines common operations for all flow handlers
   - Methods: `process()`, `validate()`, `logFlowInfo()`, `getQueueName()`

2. **AbstractFlowHandler** (`com.osb.pipeline.handler.AbstractFlowHandler`)
   - Base implementation providing common functionality
   - Delegates flow-specific logic to subclasses
   - Integrates with shared utilities for routing and transformation

3. **Flow Implementations** (Flow1Handler through Flow6Handler)
   - Each implements flow-specific validation and processing
   - Unique queue names: `flow1-queue` through `flow6-queue`
   - Flow-specific logging and error handling

4. **FlowHandlerFactory** (`com.osb.pipeline.factory.FlowHandlerFactory`)
   - Dynamically selects the correct FlowHandler based on `x-queue-name` header
   - Auto-registers all available handlers via dependency injection
   - Provides validation for queue name existence

5. **Shared Utilities**
   - **RoutingUtil**: Common routing logic across all flows
   - **TransformationUtil**: Common transformation logic across all flows

6. **REST Controller** (`com.osb.pipeline.controller.FlowController`)
   - Clean endpoint logic at `/api/flow/process`
   - Delegates all processing to appropriate FlowHandler
   - Comprehensive error handling

## Flow-Specific Behaviors

### Flow 1 (flow1-queue)
- **Validation**: Payload must be at least 10 characters
- **Processing**: Normalizes whitespace in payload

### Flow 2 (flow2-queue)
- **Validation**: Payload must contain at least one letter
- **Processing**: Converts payload to uppercase

### Flow 3 (flow3-queue)
- **Validation**: Payload must not exceed 1000 characters
- **Processing**: Adds "[FLOW3]" prefix to payload

### Flow 4 (flow4-queue)
- **Validation**: Headers are required
- **Processing**: Reverses the payload string

### Flow 5 (flow5-queue)
- **Validation**: Payload must contain "data" keyword
- **Processing**: Adds "[FLOW5]" suffix to payload

### Flow 6 (flow6-queue)
- **Validation**: Payload must be between 5 and 500 characters
- **Processing**: Encodes payload (replaces spaces with underscores, converts to lowercase)

## API Usage

### Process Flow Request

**Endpoint**: `POST /api/flow/process`

**Headers**:
- `x-queue-name`: Queue name (flow1-queue, flow2-queue, etc.)
- `Content-Type`: application/json

**Request Body**: String payload

**Example**:
```bash
curl -X POST http://localhost:8080/api/flow/process \
  -H "x-queue-name: flow1-queue" \
  -H "Content-Type: application/json" \
  -d "This is a test message for processing"
```

**Response**:
```json
{
  "status": "SUCCESS",
  "message": "Processed successfully for flow1-queue",
  "transformedPayload": "{\"requestId\":\"abc-123\",\"data\":\"processed payload\"}",
  "requestId": "abc-123"
}
```

### Health Check

**Endpoint**: `GET /api/flow/health`

**Response**:
```json
{
  "status": "UP"
}
```

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build
```bash
mvn clean install
```

### Run Tests
```bash
mvn test
```

### Run Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Project Structure

```
src/
├── main/
│   ├── java/com/osb/pipeline/
│   │   ├── PipelineFlowsApplication.java (Main application)
│   │   ├── controller/
│   │   │   └── FlowController.java (REST endpoints)
│   │   ├── factory/
│   │   │   └── FlowHandlerFactory.java (Handler selection)
│   │   ├── handler/
│   │   │   ├── FlowHandler.java (Interface)
│   │   │   ├── AbstractFlowHandler.java (Base implementation)
│   │   │   ├── Flow1Handler.java - Flow6Handler.java (Implementations)
│   │   ├── model/
│   │   │   ├── FlowRequest.java
│   │   │   └── FlowResponse.java
│   │   └── util/
│   │       ├── RoutingUtil.java
│   │       └── TransformationUtil.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/osb/pipeline/
        ├── controller/
        │   └── FlowControllerTest.java
        ├── factory/
        │   └── FlowHandlerFactoryTest.java
        └── handler/
            └── Flow1HandlerTest.java

```

## Design Principles

1. **Single Responsibility**: Each handler focuses on one flow
2. **Open/Closed**: Easy to add new flows without modifying existing code
3. **Dependency Injection**: Spring manages all component lifecycles
4. **Separation of Concerns**: Clear separation between routing, transformation, and processing
5. **Testability**: All components are easily testable with unit tests

## Error Handling

The application provides comprehensive error handling:
- **Validation Errors**: Return 400 Bad Request with descriptive message
- **Missing Queue Handler**: Return 400 Bad Request
- **Internal Errors**: Return 500 Internal Server Error
- All errors include request ID for tracking

## Logging

All flows include structured logging:
- Request received with queue name and request ID
- Flow-specific validation and processing steps
- Success/failure outcomes
- Routing destinations

Log format: `[QueueName] [RequestId: xxx] Message`
