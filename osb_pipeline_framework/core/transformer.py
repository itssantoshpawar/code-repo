"""
Transformation Engine Module

Handles data transformations in pipeline processing.
"""

from typing import Dict, Any, Callable, List, Optional
import re
import json


class TransformationError(Exception):
    """Raised when transformation fails"""
    pass


class TransformationEngine:
    """
    Executes transformations on pipeline data.
    
    Supports various transformation types including:
    - Field mapping
    - Value transformation
    - Data enrichment
    - Custom transformations
    """
    
    def __init__(self):
        self.transformations: Dict[str, Callable] = {}
        self._register_builtin_transformations()
    
    def _register_builtin_transformations(self):
        """Register built-in transformation functions"""
        self.transformations['map_fields'] = self._map_fields
        self.transformations['rename_field'] = self._rename_field
        self.transformations['add_field'] = self._add_field
        self.transformations['remove_field'] = self._remove_field
        self.transformations['transform_value'] = self._transform_value
        self.transformations['concat_fields'] = self._concat_fields
        self.transformations['split_field'] = self._split_field
        self.transformations['uppercase'] = self._uppercase
        self.transformations['lowercase'] = self._lowercase
        self.transformations['replace'] = self._replace
    
    def register_transformation(self, name: str, func: Callable):
        """
        Register a custom transformation function.
        
        Args:
            name: Name to identify the transformation
            func: Callable that takes (data, params) and returns transformed data
        """
        self.transformations[name] = func
    
    def transform(self, data: Dict[str, Any], transformation_config: Dict[str, Any]) -> Dict[str, Any]:
        """
        Apply a transformation to data based on configuration.
        
        Args:
            data: Input data to transform
            transformation_config: Configuration specifying the transformation
            
        Returns:
            Transformed data
            
        Raises:
            TransformationError: If transformation fails
        """
        transform_type = transformation_config.get('type')
        
        if not transform_type:
            raise TransformationError("Transformation type not specified")
        
        if transform_type not in self.transformations:
            raise TransformationError(f"Unknown transformation type: {transform_type}")
        
        try:
            params = transformation_config.get('params', {})
            result = self.transformations[transform_type](data.copy(), params)
            return result
        except Exception as e:
            raise TransformationError(f"Transformation '{transform_type}' failed: {str(e)}")
    
    def apply_transformations(self, data: Dict[str, Any], 
                             transformations: List[Dict[str, Any]]) -> Dict[str, Any]:
        """
        Apply a series of transformations sequentially.
        
        Args:
            data: Input data
            transformations: List of transformation configurations
            
        Returns:
            Transformed data
        """
        result = data
        for transform_config in transformations:
            result = self.transform(result, transform_config)
        return result
    
    # Built-in transformation functions
    
    def _map_fields(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Map fields according to mapping configuration"""
        mapping = params.get('mapping', {})
        result = {}
        
        for source_field, target_field in mapping.items():
            if source_field in data:
                result[target_field] = data[source_field]
        
        # Include fields not in mapping
        if params.get('include_unmapped', False):
            for key, value in data.items():
                if key not in mapping and key not in result:
                    result[key] = value
        
        return result
    
    def _rename_field(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Rename a single field"""
        old_name = params.get('old_name')
        new_name = params.get('new_name')
        
        if old_name and new_name and old_name in data:
            data[new_name] = data.pop(old_name)
        
        return data
    
    def _add_field(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Add a new field with specified value"""
        field_name = params.get('name')
        field_value = params.get('value')
        
        if field_name:
            data[field_name] = field_value
        
        return data
    
    def _remove_field(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Remove a field"""
        field_name = params.get('name')
        
        if field_name and field_name in data:
            del data[field_name]
        
        return data
    
    def _transform_value(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Transform a field's value using a function"""
        field_name = params.get('field')
        operation = params.get('operation')
        
        if field_name and field_name in data:
            if operation == 'multiply':
                data[field_name] = data[field_name] * params.get('factor', 1)
            elif operation == 'add':
                data[field_name] = data[field_name] + params.get('value', 0)
            elif operation == 'format':
                template = params.get('template', '{}')
                data[field_name] = template.format(data[field_name])
        
        return data
    
    def _concat_fields(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Concatenate multiple fields into one"""
        fields = params.get('fields', [])
        target = params.get('target')
        separator = params.get('separator', '')
        
        if target and fields:
            values = [str(data.get(field, '')) for field in fields]
            data[target] = separator.join(values)
        
        return data
    
    def _split_field(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Split a field into multiple fields"""
        field_name = params.get('field')
        separator = params.get('separator', ',')
        targets = params.get('targets', [])
        
        if field_name and field_name in data and targets:
            parts = str(data[field_name]).split(separator)
            for idx, target in enumerate(targets):
                if idx < len(parts):
                    data[target] = parts[idx].strip()
        
        return data
    
    def _uppercase(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Convert field value to uppercase"""
        field_name = params.get('field')
        
        if field_name and field_name in data and isinstance(data[field_name], str):
            data[field_name] = data[field_name].upper()
        
        return data
    
    def _lowercase(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Convert field value to lowercase"""
        field_name = params.get('field')
        
        if field_name and field_name in data and isinstance(data[field_name], str):
            data[field_name] = data[field_name].lower()
        
        return data
    
    def _replace(self, data: Dict[str, Any], params: Dict[str, Any]) -> Dict[str, Any]:
        """Replace text in a field"""
        field_name = params.get('field')
        pattern = params.get('pattern')
        replacement = params.get('replacement', '')
        
        if field_name and field_name in data and pattern:
            if isinstance(data[field_name], str):
                data[field_name] = data[field_name].replace(pattern, replacement)
        
        return data
