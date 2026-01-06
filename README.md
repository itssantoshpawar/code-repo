# MCP Service

A Spring Boot microservice implementing the Message Control Platform (MCP) with support for multiple message flows.

## Overview

This service provides message routing and transformation capabilities for various integration flows through two main services:

1. **Mcpt2rService** - Original T2R flow service (remains untouched)
2. **McpCommonService** - New service supporting 6 additional flows:
   - B2B (domain 1)
   - Siebel (domain 1)
   - Portal
   - Flow
   - NEO
   - Sync (non-queue HTTP adapter)

## Architecture

### Components

#### Services
- `Mcpt2rService` - Handles the original T2R flow
- `McpCommonService` - Handles 6 new flows with flow differentiation logic

#### Controllers
- `Mcpt2rController` - REST endpoint for T2R flow (`/api/t2r/process`)
- `McpCommonController` - REST endpoints for new flows:
  - `/api/mcp/process` - Common endpoint with flow identification
  - `/api/mcp/sync` - Dedicated sync flow endpoint (non-queue)
  - `/api/mcp/b2b` - B2B flow endpoint
  - `/api/mcp/siebel` - Siebel flow endpoint
  - `/api/mcp/portal` - Portal flow endpoint
  - `/api/mcp/flow` - Flow endpoint
  - `/api/mcp/neo` - NEO flow endpoint

#### Utilities (Shared/Reusable)
- `TransformationService` - Payload transformation logic
- `FlowIdentifier` - Flow type identification from headers/queue names
- `RoutingMapConfiguration` - Routing configuration for all flows
- `RoutingQueryConfiguration` - Query configuration for routing

#### Models
- `McpRequest` - Request model
- `McpResponse` - Response model
- `RoutingDetails` - Routing configuration details

## Flow Differentiation

The `McpCommonService` identifies flows using multiple methods (in priority order):

1. **Request Object** - `flowType` field in the request body
2. **x-queue-name Header** - Queue name containing flow identifier
3. **flow-type Header** - Explicit flow type header

### Examples

```bash
# B2B Flow with x-queue-name header
curl -X POST http://localhost:8080/api/mcp/process \
  -H "Content-Type: application/json" \
  -H "x-queue-name: b2b-domain1-queue" \
  -d '{
    "payload": "test data",
    "correlationId": "123"
  }'

# Sync Flow (dedicated endpoint)
curl -X POST http://localhost:8080/api/mcp/sync \
  -H "Content-Type: application/json" \
  -d '{
    "payload": "test data",
    "correlationId": "456"
  }'

# Portal Flow with explicit flow-type header
curl -X POST http://localhost:8080/api/mcp/process \
  -H "Content-Type: application/json" \
  -H "flow-type: portal" \
  -d '{
    "payload": "test data",
    "correlationId": "789"
  }'
```

## Configuration

Configuration is managed through `application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: mcp-service

routing:
  query:
    queries:
      t2r: "SELECT * FROM routing_table WHERE flow_type = 't2r'"
      b2b: "SELECT * FROM routing_table WHERE flow_type = 'b2b'"
      siebel: "SELECT * FROM routing_table WHERE flow_type = 'siebel'"
      portal: "SELECT * FROM routing_table WHERE flow_type = 'portal'"
      flow: "SELECT * FROM routing_table WHERE flow_type = 'flow'"
      neo: "SELECT * FROM routing_table WHERE flow_type = 'neo'"
      sync: "SELECT * FROM routing_table WHERE flow_type = 'sync'"
```

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Test
```bash
mvn test
```

## Flow Details

### Queue-Based Flows
The following flows use message queues for asynchronous processing:
- **B2B (domain 1)** - B2B integration with domain1
- **Siebel (domain 1)** - Siebel CRM integration with domain1
- **Portal** - Portal integration
- **Flow** - Generic flow processing
- **NEO** - NEO system integration

### Sync Flow (Non-Queue)
The **Sync** flow is a synchronous HTTP adapter that processes requests immediately without queuing:
- No queue involved
- Immediate response
- Dedicated endpoint: `/api/mcp/sync`

## Key Features

1. **Modular Design** - Services are self-contained and independent
2. **Reusable Components** - Utilities shared between services
3. **Flow Differentiation** - Multiple methods for identifying flow types
4. **Extensible Configuration** - Easy to add new flows
5. **Domain Support** - Supports domain-based routing (e.g., domain1)
6. **Backward Compatible** - Original Mcpt2rService remains operational

## Testing

Unit tests are provided for:
- `Mcpt2rService` - T2R flow service tests
- `McpCommonService` - Tests for all 6 new flows

Tests cover:
- Successful processing scenarios
- Error handling
- Flow identification
- Domain extraction
- Transformation logic

## Maintenance

To add a new flow:

1. Update `RoutingMapConfiguration` with new routing details
2. Add query in `RoutingQueryConfiguration`
3. Add transformation logic in `TransformationService` (if needed)
4. Add controller endpoint in `McpCommonController` (optional)
5. Update `McpCommonService.isValidFlow()` to include the new flow
6. Add tests

## License

Copyright © 2026. All rights reserved.
