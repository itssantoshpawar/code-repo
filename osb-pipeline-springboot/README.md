# OSB Pipeline Framework - Spring Boot

A flexible, configuration-driven Spring Boot framework for processing OSB (Oracle Service Bus) pipelines across multiple flows with support for transformations, schema validation, and error handling.

## Overview

This is a Spring Boot implementation of the generic OSB Pipeline Processing Framework. It provides enterprise-grade features including dependency injection, comprehensive logging, and production-ready components.

## Features

- **Spring Boot Integration**: Full Spring Boot application with auto-configuration
- **Configuration-Driven**: Define pipelines using JSON or YAML configuration files
- **Multiple Flow Support**: Process different data types through separate, configurable flows
- **Built-in Transformations**: Field mapping, value transformation, data enrichment, and more
- **Schema Validation**: Validate data at any stage using JSON Schema
- **Extensible**: Register custom transformations and stage handlers
- **Production-Ready**: Comprehensive error handling, logging with SLF4J/Logback
- **Type-Safe**: Lombok-powered POJOs with immutability support

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Maven**
- **Jackson** (JSON/YAML processing)
- **JSON Schema Validator** (networknt)
- **Lombok** (reducing boilerplate)
- **SLF4J/Logback** (logging)

## Project Structure

```
osb-pipeline-springboot/
├── src/main/java/com/osb/pipeline/
│   ├── OsbPipelineApplication.java     # Main Spring Boot application
│   ├── ExampleRunner.java              # Example flow runner
│   ├── core/
│   │   └── PipelineProcessor.java      # Pipeline stage processing
│   ├── service/
│   │   └── FlowOrchestrator.java       # Flow orchestration
│   ├── config/
│   │   └── ConfigurationManager.java   # Configuration management
│   ├── transformer/
│   │   └── TransformationEngine.java   # Data transformations
│   ├── validator/
│   │   └── SchemaValidator.java        # Schema validation
│   ├── model/
│   │   ├── FlowConfiguration.java
│   │   ├── StageConfiguration.java
│   │   ├── TransformationConfiguration.java
│   │   └── PipelineContext.java
│   └── exception/
│       ├── PipelineException.java
│       ├── ConfigurationException.java
│       ├── ValidationException.java
│       └── TransformationException.java
├── src/main/resources/
│   ├── application.properties          # Application configuration
│   └── config/
│       ├── customer-flow.json          # Example customer flow
│       └── payment-flow.json           # Example payment flow
├── src/test/java/                      # Test classes
└── pom.xml                             # Maven dependencies
```

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Build the Project

```bash
cd osb-pipeline-springboot
mvn clean install
```

### Run the Application

```bash
# Run without examples (application starts and waits)
mvn spring-boot:run

# Run with examples
mvn spring-boot:run -Dspring-boot.run.arguments=--run-examples
```

### Run as JAR

```bash
mvn clean package
java -jar target/osb-pipeline-framework-1.0.0.jar --run-examples
```

## Usage

### Basic Usage with Spring Dependency Injection

```java
@Service
@RequiredArgsConstructor
public class MyService {
    
    private final FlowOrchestrator flowOrchestrator;
    
    public void processData() {
        // Register flow from file
        flowOrchestrator.registerFlowFromFile(
            "my_flow", 
            "config/my-flow.json"
        );
        
        // Prepare input data
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("customer_id", "CUST001");
        inputData.put("name", "John Doe");
        
        // Execute flow
        Map<String, Object> result = flowOrchestrator.executeFlow(
            "my_flow", 
            inputData
        );
    }
}
```

### Programmatic Flow Registration

```java
FlowConfiguration config = FlowConfiguration.builder()
    .flowName("dynamic_flow")
    .stages(List.of(
        StageConfiguration.builder()
            .name("validation")
            .type("validation")
            .inlineSchema(schemaMap)
            .build()
    ))
    .build();

flowOrchestrator.registerFlow("dynamic_flow", config);
```

### Custom Transformation Registration

```java
@Component
public class CustomTransformations {
    
    @Autowired
    private TransformationEngine transformationEngine;
    
    @PostConstruct
    public void registerCustomTransformations() {
        transformationEngine.registerTransformation("calculate_tax", 
            (data, params) -> {
                Double taxRate = (Double) params.get("tax_rate");
                Double amount = (Double) data.get("amount");
                data.put("tax", amount * taxRate);
                data.put("total", amount + data.get("tax"));
                return data;
            }
        );
    }
}
```

## Configuration

### Flow Configuration Format

```json
{
  "flowName": "example_flow",
  "description": "Description of the flow",
  "version": "1.0.0",
  "stages": [
    {
      "name": "validation_stage",
      "type": "validation",
      "inlineSchema": {
        "required": ["field1", "field2"],
        "properties": {
          "field1": {"type": "string"},
          "field2": {"type": "integer"}
        }
      }
    },
    {
      "name": "transformation_stage",
      "type": "transformation",
      "transformations": [
        {
          "type": "uppercase",
          "params": {"field": "name"}
        }
      ]
    }
  ]
}
```

### Supported Stage Types

1. **Validation** - Validate data against JSON schemas
2. **Transformation** - Apply data transformations
3. **Enrichment** - Add additional data
4. **Routing** - Route based on data values

### Built-in Transformations

- `map_fields` - Map source fields to target fields
- `rename_field` - Rename a single field
- `add_field` - Add a new field
- `remove_field` - Remove a field
- `transform_value` - Apply mathematical operations
- `concat_fields` - Concatenate multiple fields
- `split_field` - Split a field into multiple fields
- `uppercase` - Convert to uppercase
- `lowercase` - Convert to lowercase
- `replace` - Replace text patterns

## Examples

### Example 1: Customer Data Processing

**Configuration** (`customer-flow.json`):
```json
{
  "flowName": "customer_data_processing",
  "stages": [
    {
      "name": "input_validation",
      "type": "validation",
      "inlineSchema": {
        "required": ["customer_id", "name", "email"]
      }
    },
    {
      "name": "data_transformation",
      "type": "transformation",
      "transformations": [
        {"type": "uppercase", "params": {"field": "name"}},
        {"type": "lowercase", "params": {"field": "email"}}
      ]
    }
  ]
}
```

**Usage**:
```java
Map<String, Object> customer = Map.of(
    "customer_id", "CUST001",
    "name", "john doe",
    "email", "JOHN@EXAMPLE.COM"
);

Map<String, Object> result = flowOrchestrator.executeFlow(
    "customer_data_processing", 
    customer
);
// Result: {customer_id=CUST001, name=JOHN DOE, email=john@example.com}
```

### Example 2: Payment Processing with Field Mapping

**Configuration** (`payment-flow.json`):
```json
{
  "flowName": "payment_processing",
  "stages": [
    {
      "name": "field_mapping",
      "type": "transformation",
      "transformations": [
        {
          "type": "map_fields",
          "params": {
            "mapping": {
              "txn_id": "transaction_id",
              "amt": "amount"
            }
          }
        }
      ]
    }
  ]
}
```

## Application Properties

```properties
spring.application.name=osb-pipeline-framework
logging.level.com.osb.pipeline=INFO
server.port=8080
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=FlowOrchestratorTest
```

## Building for Production

```bash
# Build executable JAR
mvn clean package -DskipTests

# Run in production
java -jar target/osb-pipeline-framework-1.0.0.jar
```

## Spring Profiles

```bash
# Development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production profile
java -jar target/osb-pipeline-framework-1.0.0.jar --spring.profiles.active=prod
```

## Logging

The framework uses SLF4J with Logback. Configure logging in `application.properties`:

```properties
logging.level.com.osb.pipeline=DEBUG
logging.file.name=logs/osb-pipeline.log
```

## Integration with Spring Boot Features

### REST API Example

```java
@RestController
@RequestMapping("/api/pipeline")
@RequiredArgsConstructor
public class PipelineController {
    
    private final FlowOrchestrator flowOrchestrator;
    
    @PostMapping("/execute/{flowName}")
    public ResponseEntity<Map<String, Object>> executeFlow(
            @PathVariable String flowName,
            @RequestBody Map<String, Object> data) {
        
        Map<String, Object> result = flowOrchestrator.executeFlow(flowName, data);
        return ResponseEntity.ok(result);
    }
}
```

### Async Processing Example

```java
@Service
public class AsyncPipelineService {
    
    @Async
    public CompletableFuture<Map<String, Object>> processAsync(
            String flowName, 
            Map<String, Object> data) {
        
        return CompletableFuture.completedFuture(
            flowOrchestrator.executeFlow(flowName, data)
        );
    }
}
```

## Best Practices

1. **Use Spring Profiles** - Separate configurations for dev, test, prod
2. **Externalize Configurations** - Store flow configs outside JAR
3. **Enable Actuator** - Monitor application health
4. **Use Async Processing** - For long-running pipelines
5. **Implement Caching** - Cache frequently used configurations
6. **Add Metrics** - Use Micrometer for monitoring

## Dependencies

All dependencies are managed through Maven. Key dependencies:

- `spring-boot-starter` - Core Spring Boot
- `spring-boot-starter-web` - REST endpoints (optional)
- `jackson-databind` - JSON processing
- `jackson-dataformat-yaml` - YAML support
- `json-schema-validator` - Schema validation
- `lombok` - Reduce boilerplate

## Comparison with Python Version

| Feature | Python Version | Spring Boot Version |
|---------|---------------|---------------------|
| Language | Python 3.7+ | Java 17+ |
| Framework | Standalone | Spring Boot |
| Dependencies | Minimal (stdlib) | Enterprise (Spring) |
| DI Container | Manual | Spring |
| REST API | Manual | Built-in |
| Logging | Python logging | SLF4J/Logback |
| Testing | pytest | JUnit 5 |
| Deployment | Script | JAR/WAR |
| Enterprise Features | Basic | Advanced |

## License

This framework is provided as-is for use in OSB pipeline processing projects.

## Support

For issues or questions, refer to the project documentation or contact the development team.
