# OSB Pipeline Framework

A flexible, configuration-driven framework for processing OSB (Oracle Service Bus) pipelines across multiple flows with support for transformations, schema validation, and error handling.

## Overview

The OSB Pipeline Framework provides a generic solution for processing data through configurable pipeline stages. It is designed to work with multiple flows without modifying existing code, making it ideal for enterprise integration scenarios.

## Features

- **Configuration-Driven**: Define pipelines using JSON or YAML configuration files
- **Multiple Flow Support**: Process different data types through separate, configurable flows
- **Built-in Transformations**: Field mapping, value transformation, data enrichment, and more
- **Schema Validation**: Validate data at any stage using JSON Schema
- **Extensible**: Register custom transformations and stage handlers
- **Error Handling**: Comprehensive error handling and logging
- **Reusable**: Generic components that can be reused across different flows

## Architecture

The framework consists of the following core components:

### Core Components

1. **PipelineProcessor**: Processes data through individual pipeline stages
2. **FlowOrchestrator**: Orchestrates multiple flows and manages their execution
3. **TransformationEngine**: Executes data transformations
4. **SchemaValidator**: Validates data against schemas
5. **ConfigurationManager**: Manages pipeline configurations

### Directory Structure

```
osb_pipeline_framework/
├── core/                      # Core processing components
│   ├── pipeline_processor.py  # Pipeline stage processing
│   ├── flow_orchestrator.py   # Flow orchestration
│   ├── transformer.py         # Data transformations
│   └── validator.py           # Schema validation
├── config/                    # Configuration management
│   └── configuration_manager.py
├── utils/                     # Utility modules
│   ├── logger.py             # Logging utilities
│   └── error_handler.py      # Error handling
├── examples/                  # Example configurations and usage
│   ├── customer_flow_config.json
│   ├── order_flow_config.json
│   ├── payment_flow_config.json
│   └── usage_examples.py
└── schemas/                   # Schema definitions
    ├── customer_schema.json
    ├── order_schema.json
    └── payment_schema.json
```

## Quick Start

### Installation

**Option 1: Direct Usage (No Installation)**
```bash
# Clone or download the repository
cd code-repo
python osb_pipeline_framework/examples/usage_examples.py
```

**Option 2: Install as Package (Recommended)**
```bash
# Install in development mode
pip install -e .

# Or install with YAML support
pip install -e .[yaml]
```

### Basic Usage

```python
from osb_pipeline_framework.core import FlowOrchestrator

# Initialize orchestrator
orchestrator = FlowOrchestrator()

# Register a flow from configuration file
orchestrator.register_flow_from_file('my_flow', 'config/my_flow_config.json')

# Prepare input data
input_data = {
    'customer_id': 'CUST001',
    'name': 'John Doe',
    'email': 'john@example.com'
}

# Execute the flow
result = orchestrator.execute_flow('my_flow', input_data)
print(result)
```

## Configuration

### Flow Configuration Format

A flow configuration defines the stages through which data will be processed:

```json
{
  "flow_name": "example_flow",
  "description": "Description of the flow",
  "version": "1.0.0",
  "stages": [
    {
      "name": "stage_name",
      "type": "stage_type",
      "description": "Stage description",
      ...stage-specific configuration...
    }
  ]
}
```

### Supported Stage Types

#### 1. Validation Stage

Validates data against a schema:

```json
{
  "name": "validation_stage",
  "type": "validation",
  "inline_schema": {
    "required": ["field1", "field2"],
    "properties": {
      "field1": {"type": "string"},
      "field2": {"type": "integer"}
    }
  }
}
```

#### 2. Transformation Stage

Applies one or more transformations:

```json
{
  "name": "transformation_stage",
  "type": "transformation",
  "transformations": [
    {
      "type": "map_fields",
      "params": {
        "mapping": {
          "old_name": "new_name"
        }
      }
    },
    {
      "type": "uppercase",
      "params": {
        "field": "name"
      }
    }
  ]
}
```

#### 3. Enrichment Stage

Adds additional data to the payload:

```json
{
  "name": "enrichment_stage",
  "type": "enrichment",
  "enrichment_data": {
    "timestamp": "2025-12-31",
    "source": "OSB_PIPELINE"
  },
  "merge_strategy": "update"
}
```

#### 4. Routing Stage

Determines routing based on data:

```json
{
  "name": "routing_stage",
  "type": "routing",
  "routing_key": "amount"
}
```

## Built-in Transformations

The framework includes these built-in transformations:

1. **map_fields**: Map fields from source to target names
2. **rename_field**: Rename a single field
3. **add_field**: Add a new field with a value
4. **remove_field**: Remove a field
5. **transform_value**: Apply mathematical operations
6. **concat_fields**: Concatenate multiple fields
7. **split_field**: Split a field into multiple fields
8. **uppercase**: Convert field value to uppercase
9. **lowercase**: Convert field value to lowercase
10. **replace**: Replace text in a field

### Transformation Examples

#### Field Mapping

```json
{
  "type": "map_fields",
  "params": {
    "mapping": {
      "txn_id": "transaction_id",
      "amt": "amount",
      "curr": "currency"
    },
    "include_unmapped": false
  }
}
```

#### String Operations

```json
{
  "type": "uppercase",
  "params": {
    "field": "customer_name"
  }
}
```

#### Field Concatenation

```json
{
  "type": "concat_fields",
  "params": {
    "fields": ["first_name", "last_name"],
    "target": "full_name",
    "separator": " "
  }
}
```

## Custom Transformations

Register custom transformation logic:

```python
from osb_pipeline_framework.core import TransformationEngine

transformer = TransformationEngine()

# Define custom transformation function
def calculate_tax(data, params):
    tax_rate = params.get('tax_rate', 0.1)
    if 'amount' in data:
        data['tax'] = data['amount'] * tax_rate
        data['total'] = data['amount'] + data['tax']
    return data

# Register the transformation
transformer.register_transformation('calculate_tax', calculate_tax)

# Use in transformation config
transformation_config = {
    'type': 'calculate_tax',
    'params': {'tax_rate': 0.08}
}
```

## Schema Validation

### Using JSON Schema

```python
from osb_pipeline_framework.core import SchemaValidator

validator = SchemaValidator()

# Define schema
schema = {
    'required': ['customer_id', 'name'],
    'properties': {
        'customer_id': {'type': 'string'},
        'name': {'type': 'string'},
        'age': {'type': 'integer'}
    }
}

# Register schema
validator.register_schema('customer', schema)

# Validate data
data = {'customer_id': 'C001', 'name': 'John', 'age': 30}
validator.validate(data, 'customer')  # Returns True or raises ValidationError
```

## Error Handling

The framework provides comprehensive error handling:

```python
from osb_pipeline_framework.utils import ErrorHandler

error_handler = ErrorHandler()

# Register custom error handler
def handle_validation_error(error, error_info):
    print(f"Validation failed: {error_info['error_message']}")
    # Send notification, log to external system, etc.

error_handler.register_error_handler('ValidationError', handle_validation_error)

# Handle errors
try:
    result = orchestrator.execute_flow('my_flow', data)
except Exception as e:
    error_info = error_handler.handle_error(e, context={'flow': 'my_flow'})
```

## Multiple Flows

Execute multiple flows sequentially:

```python
orchestrator = FlowOrchestrator()

# Register multiple flows
orchestrator.register_flow_from_file('customer_flow', 'customer_config.json')
orchestrator.register_flow_from_file('payment_flow', 'payment_config.json')

# Execute multiple flows
flow_configs = [
    {'flow_name': 'customer_flow', 'input_data': customer_data},
    {'flow_name': 'payment_flow', 'input_data': payment_data}
]

results = orchestrator.execute_multiple_flows(flow_configs)
```

## Examples

The `examples` directory contains complete working examples:

1. **customer_flow_config.json**: Customer data processing with validation and transformation
2. **order_flow_config.json**: Order processing with routing
3. **payment_flow_config.json**: Payment processing with field mapping
4. **usage_examples.py**: Python script demonstrating all features

Run the examples:

```bash
cd osb_pipeline_framework/examples
python usage_examples.py
```

## Logging

Configure logging for pipeline execution:

```python
from osb_pipeline_framework.utils import setup_logger

# Set up logger with file output
logger = setup_logger(
    name='my_pipeline',
    level=logging.INFO,
    log_file='pipeline.log'
)
```

## Best Practices

1. **Keep Flows Focused**: Each flow should handle a specific business process
2. **Validate Early**: Add validation stages at the beginning of flows
3. **Use Schemas**: Define and reuse schemas for consistent validation
4. **Modular Transformations**: Break complex transformations into smaller steps
5. **Error Handling**: Configure appropriate error handling for each flow
6. **Logging**: Enable logging for production environments
7. **Configuration Management**: Store configurations in version control
8. **Testing**: Test flows with various input scenarios

## Extending the Framework

### Custom Stage Handlers

```python
from osb_pipeline_framework.core import PipelineProcessor

processor = PipelineProcessor()

# Define custom stage handler
def custom_stage_handler(data, stage_config, context):
    # Custom processing logic
    result = data.copy()
    result['custom_field'] = 'custom_value'
    return result

# Register the handler
processor.register_stage_handler('custom_stage', custom_stage_handler)
```

### Custom Validators

Extend the SchemaValidator class to implement custom validation logic.

## Requirements

- Python 3.7+
- PyYAML (optional, for YAML configuration support)

## License

This framework is provided as-is for use in OSB pipeline processing projects.

## Support

For issues, questions, or contributions, please refer to the project documentation or contact the development team.
