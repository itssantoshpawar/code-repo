# Configuration Guide

## OSB Pipeline Framework Configuration Guide

This guide provides detailed information on configuring flows, stages, transformations, and schemas for the OSB Pipeline Framework.

## Configuration File Formats

The framework supports two configuration file formats:

1. **JSON**: `.json` files
2. **YAML**: `.yaml` or `.yml` files (requires PyYAML)

## Flow Configuration Structure

### Basic Structure

```json
{
  "flow_name": "string",           // Required: Unique flow identifier
  "description": "string",          // Optional: Flow description
  "version": "string",              // Optional: Version number
  "stages": [                       // Required: Array of stage configurations
    {
      "name": "string",             // Required: Stage name
      "type": "string",             // Required: Stage type
      "description": "string"       // Optional: Stage description
      // ...stage-specific configuration...
    }
  ],
  "error_handling": {               // Optional: Error handling configuration
    "on_error": "string",           // Optional: Error handling strategy
    "retry_count": integer          // Optional: Number of retries
  }
}
```

## Stage Types

### 1. Validation Stage

Validates data against a schema at any point in the pipeline.

#### Configuration Options

```json
{
  "name": "validation_stage_name",
  "type": "validation",
  "schema": "schema_name"           // Reference to registered schema
}
```

OR

```json
{
  "name": "validation_stage_name",
  "type": "validation",
  "inline_schema": {                // Inline schema definition
    "required": ["field1", "field2"],
    "properties": {
      "field1": {
        "type": "string"
      },
      "field2": {
        "type": "integer"
      }
    }
  }
}
```

#### Schema Property Types

- `string`: Text data
- `integer`: Whole numbers
- `number`: Numeric data (integers or floats)
- `boolean`: True/false values
- `array`: Lists
- `object`: Nested objects
- `null`: Null values

### 2. Transformation Stage

Applies one or more transformations to data.

#### Configuration

```json
{
  "name": "transformation_stage_name",
  "type": "transformation",
  "transformations": [
    {
      "type": "transformation_type",
      "params": {
        // transformation-specific parameters
      }
    }
  ]
}
```

#### Available Transformations

##### map_fields

Maps source fields to target fields.

```json
{
  "type": "map_fields",
  "params": {
    "mapping": {
      "source_field1": "target_field1",
      "source_field2": "target_field2"
    },
    "include_unmapped": false    // Optional: include fields not in mapping
  }
}
```

##### rename_field

Renames a single field.

```json
{
  "type": "rename_field",
  "params": {
    "old_name": "old_field_name",
    "new_name": "new_field_name"
  }
}
```

##### add_field

Adds a new field with a specified value.

```json
{
  "type": "add_field",
  "params": {
    "name": "new_field",
    "value": "field_value"        // Can be string, number, boolean, etc.
  }
}
```

##### remove_field

Removes a field from data.

```json
{
  "type": "remove_field",
  "params": {
    "name": "field_to_remove"
  }
}
```

##### transform_value

Applies mathematical operations to a field.

```json
{
  "type": "transform_value",
  "params": {
    "field": "amount",
    "operation": "multiply",      // Options: multiply, add, format
    "factor": 1.1                 // For multiply
    // "value": 10                // For add
    // "template": "${}"          // For format
  }
}
```

##### concat_fields

Concatenates multiple fields into one.

```json
{
  "type": "concat_fields",
  "params": {
    "fields": ["first_name", "last_name"],
    "target": "full_name",
    "separator": " "              // Optional: separator between values
  }
}
```

##### split_field

Splits a field into multiple fields.

```json
{
  "type": "split_field",
  "params": {
    "field": "full_name",
    "separator": " ",
    "targets": ["first_name", "last_name"]
  }
}
```

##### uppercase

Converts field value to uppercase.

```json
{
  "type": "uppercase",
  "params": {
    "field": "name"
  }
}
```

##### lowercase

Converts field value to lowercase.

```json
{
  "type": "lowercase",
  "params": {
    "field": "email"
  }
}
```

##### replace

Replaces text in a field.

```json
{
  "type": "replace",
  "params": {
    "field": "description",
    "pattern": "old_text",
    "replacement": "new_text"
  }
}
```

### 3. Enrichment Stage

Adds additional data to the payload.

#### Configuration

```json
{
  "name": "enrichment_stage_name",
  "type": "enrichment",
  "enrichment_data": {
    "field1": "value1",
    "field2": "value2"
  },
  "merge_strategy": "update"      // Options: update, append
}
```

**Merge Strategies:**
- `update`: Overwrites existing fields
- `append`: Only adds fields that don't exist

### 4. Routing Stage

Determines routing based on data fields.

#### Configuration

```json
{
  "name": "routing_stage_name",
  "type": "routing",
  "routing_key": "field_name"     // Field to use for routing decision
}
```

The routing decision is stored in the context for downstream processing.

## Schema Definitions

### JSON Schema Format

Schemas follow JSON Schema Draft 7 specification.

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Schema Title",
  "description": "Schema description",
  "type": "object",
  "required": ["field1", "field2"],
  "properties": {
    "field1": {
      "type": "string",
      "description": "Field description",
      "minLength": 1
    },
    "field2": {
      "type": "integer",
      "minimum": 0,
      "maximum": 100
    },
    "field3": {
      "type": "string",
      "enum": ["option1", "option2", "option3"]
    },
    "nested_object": {
      "type": "object",
      "properties": {
        "sub_field": {
          "type": "string"
        }
      }
    },
    "array_field": {
      "type": "array",
      "items": {
        "type": "string"
      }
    }
  }
}
```

## Complete Flow Examples

### Example 1: Simple Validation Flow

```json
{
  "flow_name": "simple_validation",
  "version": "1.0.0",
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
    }
  ]
}
```

### Example 2: Transformation Pipeline

```json
{
  "flow_name": "data_standardization",
  "version": "1.0.0",
  "stages": [
    {
      "name": "normalize_fields",
      "type": "transformation",
      "transformations": [
        {
          "type": "lowercase",
          "params": {"field": "email"}
        },
        {
          "type": "uppercase",
          "params": {"field": "country_code"}
        },
        {
          "type": "add_field",
          "params": {
            "name": "processed_date",
            "value": "2025-12-31"
          }
        }
      ]
    }
  ]
}
```

### Example 3: Complete ETL Flow

```json
{
  "flow_name": "etl_pipeline",
  "description": "Extract, Transform, Load pipeline",
  "version": "1.0.0",
  "stages": [
    {
      "name": "validate_source",
      "type": "validation",
      "inline_schema": {
        "required": ["source_id", "data"],
        "properties": {
          "source_id": {"type": "string"},
          "data": {"type": "object"}
        }
      }
    },
    {
      "name": "transform_data",
      "type": "transformation",
      "transformations": [
        {
          "type": "map_fields",
          "params": {
            "mapping": {
              "src_id": "id",
              "src_name": "name",
              "src_val": "value"
            }
          }
        }
      ]
    },
    {
      "name": "enrich_metadata",
      "type": "enrichment",
      "enrichment_data": {
        "pipeline": "etl",
        "version": "1.0.0"
      }
    },
    {
      "name": "validate_output",
      "type": "validation",
      "inline_schema": {
        "required": ["id", "name", "value"],
        "properties": {
          "id": {"type": "string"},
          "name": {"type": "string"},
          "value": {"type": "number"}
        }
      }
    }
  ]
}
```

## Configuration Best Practices

1. **Use Descriptive Names**: Give stages and flows meaningful names
2. **Document Configurations**: Use description fields
3. **Version Control**: Use version numbers for flows
4. **Validate Early**: Place validation stages at the start
5. **Modular Stages**: Keep stages focused on single responsibilities
6. **Reuse Schemas**: Store common schemas separately and reference them
7. **Error Handling**: Configure appropriate error handling strategies
8. **Test Configurations**: Test with various data scenarios

## Loading Configurations

### From File

```python
orchestrator.register_flow_from_file('flow_name', 'path/to/config.json')
```

### From Dictionary

```python
config = {
    "flow_name": "my_flow",
    "stages": [...]
}
orchestrator.register_flow('my_flow', config)
```

## Configuration Validation

The framework validates configurations automatically:

- Checks for required fields (flow_name, stages)
- Validates stage structure (name, type)
- Ensures stages is an array

Additional validation can be added by extending the ConfigurationManager class.
