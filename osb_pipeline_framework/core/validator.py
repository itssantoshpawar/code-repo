"""
Schema Validator Module

Provides flexible schema validation for pipeline data at various stages.
"""

import json
from typing import Dict, Any, List, Optional
from pathlib import Path


class ValidationError(Exception):
    """Raised when validation fails"""
    pass


class SchemaValidator:
    """
    Validates data against defined schemas.
    
    Supports simple field validation and JSON Schema validation.
    Can be extended to support additional validation frameworks.
    """
    
    def __init__(self):
        self.schemas: Dict[str, Dict[str, Any]] = {}
    
    def register_schema(self, schema_name: str, schema: Dict[str, Any]):
        """
        Register a schema for validation.
        
        Args:
            schema_name: Name to identify the schema
            schema: Schema definition dictionary
        """
        self.schemas[schema_name] = schema
    
    def load_schema_from_file(self, schema_path: str, schema_name: str):
        """
        Load schema from a JSON file.
        
        Args:
            schema_path: Path to the schema file
            schema_name: Name to identify the schema
            
        Raises:
            ValidationError: If schema cannot be loaded
        """
        path = Path(schema_path)
        
        if not path.exists():
            raise ValidationError(f"Schema file not found: {schema_path}")
        
        try:
            with open(path, 'r') as f:
                schema = json.load(f)
            self.register_schema(schema_name, schema)
        except Exception as e:
            raise ValidationError(f"Error loading schema: {str(e)}")
    
    def validate(self, data: Dict[str, Any], schema_name: str) -> bool:
        """
        Validate data against a registered schema.
        
        Args:
            data: Data to validate
            schema_name: Name of the schema to validate against
            
        Returns:
            True if validation passes
            
        Raises:
            ValidationError: If validation fails
        """
        if schema_name not in self.schemas:
            raise ValidationError(f"Schema not found: {schema_name}")
        
        schema = self.schemas[schema_name]
        
        # Perform basic field validation
        errors = self._validate_fields(data, schema)
        
        if errors:
            raise ValidationError(f"Validation failed: {', '.join(errors)}")
        
        return True
    
    def _validate_fields(self, data: Dict[str, Any], schema: Dict[str, Any]) -> List[str]:
        """
        Validate data fields against schema definition.
        
        Args:
            data: Data to validate
            schema: Schema definition
            
        Returns:
            List of validation error messages
        """
        errors = []
        
        # Check required fields
        required_fields = schema.get('required', [])
        for field in required_fields:
            if field not in data:
                errors.append(f"Missing required field: {field}")
        
        # Check field types
        properties = schema.get('properties', {})
        for field, field_schema in properties.items():
            if field in data:
                expected_type = field_schema.get('type')
                if expected_type:
                    if not self._check_type(data[field], expected_type):
                        errors.append(f"Field '{field}' has invalid type. Expected: {expected_type}")
        
        return errors
    
    def _check_type(self, value: Any, expected_type: str) -> bool:
        """
        Check if a value matches the expected type.
        
        Args:
            value: Value to check
            expected_type: Expected type string
            
        Returns:
            True if type matches
        """
        type_mapping = {
            'string': str,
            'integer': int,
            'number': (int, float),
            'boolean': bool,
            'array': list,
            'object': dict,
            'null': type(None)
        }
        
        expected_python_type = type_mapping.get(expected_type)
        if expected_python_type is None:
            return True  # Unknown type, skip validation
        
        return isinstance(value, expected_python_type)
    
    def validate_with_schema(self, data: Dict[str, Any], schema: Dict[str, Any]) -> bool:
        """
        Validate data against an inline schema (without registration).
        
        Args:
            data: Data to validate
            schema: Schema definition
            
        Returns:
            True if validation passes
            
        Raises:
            ValidationError: If validation fails
        """
        errors = self._validate_fields(data, schema)
        
        if errors:
            raise ValidationError(f"Validation failed: {', '.join(errors)}")
        
        return True
