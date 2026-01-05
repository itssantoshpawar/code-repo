# OSB Pipeline Flows - Spring Boot Implementation

This project implements 6 Oracle Service Bus (OSB) style pipeline flows using Spring Boot, providing a robust message processing framework with validation, transformation, and routing capabilities.

## Overview

The application processes messages from RabbitMQ queues, determining the appropriate pipeline flow based on the `x-queue-name` header or the actual queue name. Each pipeline can include:

- **Logging**: Comprehensive logging at each stage
- **Validation**: Basic message validation
- **Schema Validation**: XML Schema (XSD) validation
- **XQuery Transformation**: XML transformations using XQuery
- **Routing**: Messages can be routed to HTTP services or other queues

## Architecture

### Core Components

1. **PipelineMessageListener**: Listens to multiple queues and extracts the `x-queue-name` header
2. **PipelineOrchestrationService**: Coordinates the entire pipeline execution
3. **ValidationService**: Performs basic message validation
4. **SchemaValidationService**: Validates XML against XSD schemas
5. **XQueryTransformationService**: Transforms XML using XQuery scripts
6. **RoutingService**: Routes messages to services (HTTP) or queues (RabbitMQ)
7. **PipelineLoggingService**: Provides comprehensive logging throughout pipeline execution

## Six Pipeline Flows

### 1. Order Processing Pipeline
- **Queue**: `order-processing-queue`
- **Features**: Validation, Schema Validation, XQuery Transformation
- **Schema**: `schemas/order-schema.xsd`
- **Transform**: `xqueries/order-transform.xq`
- **Route**: HTTP Service → `http://localhost:8081/orders`

### 2. Payment Processing Pipeline
- **Queue**: `payment-processing-queue`
- **Features**: Validation, Schema Validation, XQuery Transformation
- **Schema**: `schemas/payment-schema.xsd`
- **Transform**: `xqueries/payment-transform.xq`
- **Route**: HTTP Service → `http://localhost:8082/payments`

### 3. Customer Notification Pipeline
- **Queue**: `customer-notification-queue`
- **Features**: Validation only
- **Route**: Queue → `notification-output-queue`

### 4. Inventory Update Pipeline
- **Queue**: `inventory-update-queue`
- **Features**: Validation, Schema Validation, XQuery Transformation
- **Schema**: `schemas/inventory-schema.xsd`
- **Transform**: `xqueries/inventory-transform.xq`
- **Route**: HTTP Service → `http://localhost:8083/inventory`

### 5. Audit Logging Pipeline
- **Queue**: `audit-logging-queue`
- **Features**: Validation only
- **Route**: Queue → `audit-output-queue`

### 6. Error Handling Pipeline
- **Queue**: `error-handling-queue`
- **Features**: Validation, XQuery Transformation
- **Transform**: `xqueries/error-transform.xq`
- **Route**: Queue → `error-output-queue`

## Configuration

All pipeline configurations are defined in `src/main/resources/application.yml`:

```yaml
osb:
  pipelines:
    - queue-name: order-processing-queue
      name: Order Processing Pipeline
      validation-enabled: true
      schema-validation-enabled: true
      schema-path: schemas/order-schema.xsd
      xquery-enabled: true
      xquery-path: xqueries/order-transform.xq
      route-type: SERVICE
      route-destination: http://localhost:8081/orders
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- RabbitMQ server running on localhost:5672
- (Optional) Target HTTP services running for service-based routing

## Building the Project

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/osb-pipeline-flows-1.0.0.jar
```

## Environment Variables

The application supports the following environment variables for configuration:

- `RABBITMQ_HOST`: RabbitMQ server host (default: localhost)
- `RABBITMQ_PORT`: RabbitMQ server port (default: 5672)
- `RABBITMQ_USERNAME`: RabbitMQ username (default: guest)
- `RABBITMQ_PASSWORD`: RabbitMQ password (default: guest)

Example with environment variables:

```bash
export RABBITMQ_HOST=rabbitmq.example.com
export RABBITMQ_PORT=5672
export RABBITMQ_USERNAME=myuser
export RABBITMQ_PASSWORD=mypassword
java -jar target/osb-pipeline-flows-1.0.0.jar
```

## Message Flow

1. **Message Reception**: Message arrives on one of the 6 input queues
2. **Queue Determination**: System reads `x-queue-name` header or uses actual queue name
3. **Pipeline Selection**: Appropriate pipeline configuration is selected
4. **Validation**: Basic validation checks (if enabled)
5. **Schema Validation**: XML validated against XSD (if enabled)
6. **Transformation**: XQuery transformation applied (if enabled)
7. **Routing**: Message routed to target service or queue
8. **Logging**: Comprehensive logging at each step

## Example Message

### Order Processing Example

Send to queue: `order-processing-queue`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Order>
    <orderId>ORD-12345</orderId>
    <customerId>CUST-67890</customerId>
    <orderDate>2026-01-05</orderDate>
    <totalAmount>299.99</totalAmount>
    <items>
        <item>
            <productId>PROD-001</productId>
            <quantity>2</quantity>
            <price>99.99</price>
        </item>
        <item>
            <productId>PROD-002</productId>
            <quantity>1</quantity>
            <price>100.01</price>
        </item>
    </items>
</Order>
```

With optional header:
```
x-queue-name: order-processing-queue
```

## Testing with RabbitMQ

You can send test messages using the RabbitMQ Management UI or command line:

```bash
# Using rabbitmqadmin
rabbitmqadmin publish exchange=amq.default routing_key=order-processing-queue payload="<Order>...</Order>"
```

## Project Structure

```
src/
├── main/
│   ├── java/com/osb/pipeline/
│   │   ├── OsbPipelineApplication.java          # Main application
│   │   ├── config/
│   │   │   ├── PipelineProperties.java          # Pipeline configuration
│   │   │   ├── RabbitMQConfig.java              # RabbitMQ setup
│   │   │   └── RestTemplateConfig.java          # HTTP client setup
│   │   ├── listener/
│   │   │   └── PipelineMessageListener.java     # Message listener
│   │   ├── model/
│   │   │   ├── PipelineMessage.java             # Message model
│   │   │   └── PipelineExecution.java           # Execution tracking
│   │   └── service/
│   │       ├── PipelineOrchestrationService.java # Main orchestrator
│   │       ├── PipelineLoggingService.java       # Logging service
│   │       ├── ValidationService.java            # Validation
│   │       ├── SchemaValidationService.java      # XSD validation
│   │       ├── XQueryTransformationService.java  # XQuery transforms
│   │       └── RoutingService.java               # Message routing
│   └── resources/
│       ├── application.yml                       # Application config
│       ├── schemas/                              # XSD schemas
│       │   ├── order-schema.xsd
│       │   ├── payment-schema.xsd
│       │   └── inventory-schema.xsd
│       └── xqueries/                             # XQuery files
│           ├── order-transform.xq
│           ├── payment-transform.xq
│           ├── inventory-transform.xq
│           └── error-transform.xq
```

## Extending the Solution

### Adding a New Pipeline

1. Add configuration in `application.yml`:
```yaml
- queue-name: new-pipeline-queue
  name: New Pipeline
  validation-enabled: true
  # ... other settings
```

2. Add queue bean in `RabbitMQConfig.java`:
```java
@Bean
public Queue newPipelineQueue() {
    return new Queue("new-pipeline-queue", true);
}
```

3. Update listener in `PipelineMessageListener.java`:
```java
@RabbitListener(queues = {
    "order-processing-queue",
    "payment-processing-queue",
    // ... existing queues
    "new-pipeline-queue"  // Add new queue
})
```

4. (Optional) Add schema and XQuery files if needed

## Design Principles

- **Simple and Intuitive**: Clear separation of concerns with single-responsibility services
- **Reusable Components**: All services are reusable across different pipelines
- **Spring Boot Conventions**: Follows standard Spring Boot patterns and best practices
- **Maintainable**: Configuration-driven approach for easy pipeline management
- **Extensible**: Easy to add new pipelines or modify existing ones

## Error Handling

Errors are logged comprehensively with:
- Execution ID for traceability
- Pipeline name and queue information
- Full stack traces
- Execution duration

Failed messages can be routed to error queues or Dead Letter Queues (DLQ) for manual inspection and reprocessing.

## Logging

The application provides detailed logging at DEBUG level for `com.osb.pipeline` package, including:
- Pipeline start/completion
- Each processing step (validation, transformation, routing)
- Execution duration
- Error details with stack traces

## Dependencies

- **Spring Boot 3.2.0**: Core framework
- **Spring AMQP**: RabbitMQ integration
- **Saxon-HE 12.3**: XQuery processing
- **Lombok**: Reduces boilerplate code
- **Spring Validation**: Message validation

## License

This project is provided as-is for educational and commercial use.
