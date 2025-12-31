# Code Repository

This repository contains **OSB Pipeline Framework** implementations - generic, configuration-driven frameworks for processing OSB (Oracle Service Bus) pipelines across multiple flows.

## Contents

- `osb-pipeline-springboot/` - **Spring Boot/Java implementation** (NEW!)
- `osb_pipeline_framework/` - Python implementation
- `temp.txt` - Original repository file (unchanged)

## OSB Pipeline Framework - Spring Boot (Java)

⭐ **Primary Implementation** - Enterprise-grade Spring Boot framework for OSB pipeline processing.

### Technology Stack
- **Java 17**
- **Spring Boot 3.2.1**
- **Maven**
- **Jackson** (JSON/YAML)
- **JSON Schema Validator**

### Quick Start

```bash
cd osb-pipeline-springboot
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments=--run-examples
```

### Features

✅ **Spring Boot Integration**: Full enterprise features with dependency injection  
✅ **Configuration-Driven**: Define pipelines using JSON or YAML  
✅ **Multiple Flow Support**: Handle different data types with separate flows  
✅ **Built-in Transformations**: Field mapping, validation, enrichment, and more  
✅ **Schema Validation**: Validate data at any stage  
✅ **Production-Ready**: Comprehensive error handling, logging  
✅ **Type-Safe**: Lombok-powered POJOs  

### Documentation

- **[Spring Boot README](osb-pipeline-springboot/README.md)** - Complete documentation
- **[Examples](osb-pipeline-springboot/src/main/resources/config/)** - Flow configurations

---

## OSB Pipeline Framework - Python

Alternative Python implementation for lightweight deployments.

### Quick Start

```bash
cd osb_pipeline_framework/examples
python usage_examples.py
```

### Documentation

- **[Quick Start Guide](osb_pipeline_framework/QUICKSTART.md)** - Get started in 5 minutes
- **[Full Documentation](osb_pipeline_framework/README.md)** - Complete feature guide
- **[Configuration Guide](osb_pipeline_framework/CONFIGURATION_GUIDE.md)** - Detailed configuration options

---

## Comparison

| Feature | Spring Boot (Java) | Python |
|---------|-------------------|--------|
| Language | Java 17 | Python 3.7+ |
| Framework | Spring Boot | Standalone |
| Dependencies | Maven | Minimal |
| DI Container | Spring | Manual |
| REST API | Built-in | Manual |
| Enterprise Ready | ✅ Yes | Basic |
| Deployment | JAR/WAR | Script |

## Project Structure

```
code-repo/
├── osb-pipeline-springboot/       # Spring Boot implementation
│   ├── src/main/java/             # Java source code
│   ├── src/main/resources/        # Configurations
│   ├── pom.xml                    # Maven dependencies
│   └── README.md                  # Documentation
├── osb_pipeline_framework/        # Python implementation
│   ├── core/                      # Core components
│   ├── examples/                  # Example configurations
│   └── README.md                  # Documentation
└── temp.txt                       # Original file (UNCHANGED)
```

## Use Cases

Both implementations are ideal for:

- **Data Integration**: Transform data between different systems
- **ETL Pipelines**: Extract, transform, and load data flows
- **API Gateway**: Process and validate API requests/responses
- **Message Processing**: Handle message transformations in service buses
- **Data Validation**: Validate data against schemas
- **Field Mapping**: Map fields between different formats

## Design Principles

1. **Separation of Concerns**: Existing code remains untouched
2. **Configuration Over Code**: Define flows through configuration
3. **Reusability**: Components can be reused across different flows
4. **Extensibility**: Easy to add custom logic
5. **Maintainability**: Clear structure and documentation

## Requirements

### Spring Boot Version
- Java 17 or higher
- Maven 3.6+

### Python Version
- Python 3.7 or higher
- PyYAML (optional, for YAML configuration support)

## License

This framework is provided as-is for use in OSB pipeline processing projects.
