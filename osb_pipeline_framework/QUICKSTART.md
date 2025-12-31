# Quick Start Guide - OSB Pipeline Framework

This guide will help you get started with the OSB Pipeline Framework in just a few minutes.

## Installation

1. **No installation required!** The framework uses only Python standard library.
2. Optionally install PyYAML for YAML configuration support:

```bash
pip install PyYAML
```

## 5-Minute Tutorial

### Step 1: Create a Simple Flow Configuration

Create a file `my_first_flow.json`:

```json
{
  "flow_name": "my_first_flow",
  "description": "My first OSB pipeline flow",
  "stages": [
    {
      "name": "validate_input",
      "type": "validation",
      "inline_schema": {
        "required": ["id", "name"],
        "properties": {
          "id": {"type": "string"},
          "name": {"type": "string"}
        }
      }
    },
    {
      "name": "transform_data",
      "type": "transformation",
      "transformations": [
        {
          "type": "uppercase",
          "params": {"field": "name"}
        },
        {
          "type": "add_field",
          "params": {
            "name": "status",
            "value": "processed"
          }
        }
      ]
    }
  ]
}
```

### Step 2: Write a Simple Python Script

Create a file `run_flow.py`:

```python
import sys
import json
from pathlib import Path

# Add the framework to path
sys.path.insert(0, str(Path(__file__).parent))

from osb_pipeline_framework.core import FlowOrchestrator

# Initialize orchestrator
orchestrator = FlowOrchestrator()

# Register your flow
orchestrator.register_flow_from_file('my_first_flow', 'my_first_flow.json')

# Prepare input data
input_data = {
    'id': 'USER001',
    'name': 'john doe'
}

print("Input:", json.dumps(input_data, indent=2))

# Execute the flow
result = orchestrator.execute_flow('my_first_flow', input_data)

print("\nOutput:", json.dumps(result, indent=2))
```

### Step 3: Run It!

```bash
python run_flow.py
```

**Expected Output:**

```
Input: {
  "id": "USER001",
  "name": "john doe"
}

Output: {
  "id": "USER001",
  "name": "JOHN DOE",
  "status": "processed"
}
```

## Common Use Cases

### Use Case 1: Data Validation

```json
{
  "flow_name": "validate_customer",
  "stages": [
    {
      "name": "validate",
      "type": "validation",
      "inline_schema": {
        "required": ["customer_id", "email"],
        "properties": {
          "customer_id": {"type": "string"},
          "email": {"type": "string"}
        }
      }
    }
  ]
}
```

### Use Case 2: Field Mapping

```json
{
  "flow_name": "map_fields",
  "stages": [
    {
      "name": "mapping",
      "type": "transformation",
      "transformations": [
        {
          "type": "map_fields",
          "params": {
            "mapping": {
              "old_field": "new_field",
              "legacy_id": "id"
            }
          }
        }
      ]
    }
  ]
}
```

### Use Case 3: Data Enrichment

```json
{
  "flow_name": "enrich_data",
  "stages": [
    {
      "name": "add_metadata",
      "type": "enrichment",
      "enrichment_data": {
        "timestamp": "2025-12-31",
        "source": "API"
      }
    }
  ]
}
```

## Next Steps

1. **Explore Examples**: Check out the `examples` directory for more complex scenarios
2. **Read Documentation**: See [README.md](README.md) for complete feature list
3. **Configuration Guide**: Read [CONFIGURATION_GUIDE.md](CONFIGURATION_GUIDE.md) for all options
4. **Custom Transformations**: Learn how to add your own transformation logic

## Need Help?

- Review the example configurations in `examples/` directory
- Run `python examples/usage_examples.py` to see the framework in action
- Check the inline documentation in the source code

## Tips for Success

1. **Start Simple**: Begin with validation-only flows
2. **Test Incrementally**: Add one stage at a time
3. **Use Descriptive Names**: Name your stages clearly
4. **Validate Often**: Add validation stages between transformations
5. **Keep Flows Focused**: One flow per business process

Happy pipelining! 🚀
