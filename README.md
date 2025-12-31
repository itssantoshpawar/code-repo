# Code Repository

This repository contains the **OSB Pipeline Framework** - a generic, configuration-driven framework for processing OSB (Oracle Service Bus) pipelines across multiple flows.

## Contents

- `osb_pipeline_framework/` - Generic OSB pipeline processing framework
- `temp.txt` - Original repository file (unchanged)

## OSB Pipeline Framework

A flexible, reusable framework designed to process any OSB pipeline generically without modifying existing code.

### Key Features

✅ **Configuration-Driven**: Define pipelines using JSON or YAML  
✅ **Multiple Flow Support**: Handle different data types with separate flows  
✅ **Built-in Transformations**: Field mapping, validation, enrichment, and more  
✅ **Schema Validation**: Validate data at any stage  
✅ **Extensible**: Add custom transformations and handlers  
✅ **No Dependencies**: Uses only Python standard library  

### Quick Start

```python
from osb_pipeline_framework.core import FlowOrchestrator

# Initialize and register a flow
orchestrator = FlowOrchestrator()
orchestrator.register_flow_from_file('my_flow', 'config.json')

# Process data
result = orchestrator.execute_flow('my_flow', input_data)
```

### Documentation

- **[Quick Start Guide](osb_pipeline_framework/QUICKSTART.md)** - Get started in 5 minutes
- **[Full Documentation](osb_pipeline_framework/README.md)** - Complete feature guide
- **[Configuration Guide](osb_pipeline_framework/CONFIGURATION_GUIDE.md)** - Detailed configuration options
- **[Examples](osb_pipeline_framework/examples/)** - Working examples and usage patterns

### Run Examples

```bash
cd osb_pipeline_framework/examples
python usage_examples.py
```

## Project Structure

```
code-repo/
├── osb_pipeline_framework/      # Generic OSB pipeline framework
│   ├── core/                    # Core processing components
│   │   ├── pipeline_processor.py
│   │   ├── flow_orchestrator.py
│   │   ├── transformer.py
│   │   └── validator.py
│   ├── config/                  # Configuration management
│   │   └── configuration_manager.py
│   ├── utils/                   # Utilities
│   │   ├── logger.py
│   │   └── error_handler.py
│   ├── examples/                # Example configurations
│   │   ├── customer_flow_config.json
│   │   ├── order_flow_config.json
│   │   ├── payment_flow_config.json
│   │   └── usage_examples.py
│   ├── schemas/                 # Schema definitions
│   │   ├── customer_schema.json
│   │   ├── order_schema.json
│   │   └── payment_schema.json
│   ├── README.md               # Framework documentation
│   ├── QUICKSTART.md           # Quick start guide
│   ├── CONFIGURATION_GUIDE.md  # Configuration reference
│   └── requirements.txt        # Python dependencies
└── temp.txt                    # Original file (unchanged)
```

## Framework Architecture

```
┌─────────────────────────────────────────┐
│       Flow Orchestrator                 │
│  (Manages multiple flows)               │
└───────────────┬─────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│      Pipeline Processor                 │
│  (Processes individual stages)          │
└───────────────┬─────────────────────────┘
                │
        ┌───────┴───────┬────────────────┐
        ▼               ▼                ▼
┌──────────────┐ ┌─────────────┐ ┌──────────────┐
│ Transformer  │ │  Validator  │ │    Config    │
│  (Transform  │ │  (Validate  │ │  (Manage     │
│   data)      │ │   schema)   │ │   configs)   │
└──────────────┘ └─────────────┘ └──────────────┘
```

## Use Cases

The framework is ideal for:

- **Data Integration**: Transform data between different systems
- **ETL Pipelines**: Extract, transform, and load data flows
- **API Gateway**: Process and validate API requests/responses
- **Message Processing**: Handle message transformations in service buses
- **Data Validation**: Validate data against schemas
- **Field Mapping**: Map fields between different formats

## Requirements

- Python 3.7 or higher
- PyYAML (optional, for YAML configuration support)

## Design Principles

1. **Separation of Concerns**: Existing code remains untouched
2. **Configuration Over Code**: Define flows through configuration
3. **Reusability**: Components can be reused across different flows
4. **Extensibility**: Easy to add custom logic
5. **Maintainability**: Clear structure and documentation

## License

This framework is provided as-is for use in OSB pipeline processing projects.
