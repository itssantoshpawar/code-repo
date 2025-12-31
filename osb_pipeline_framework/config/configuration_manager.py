"""
Configuration Management Module

Handles loading and validation of pipeline configurations from various sources.
"""

import json
import yaml
from typing import Dict, Any, List, Optional
from pathlib import Path


class ConfigurationError(Exception):
    """Raised when configuration is invalid or cannot be loaded"""
    pass


class ConfigurationManager:
    """
    Manages pipeline configurations from JSON, YAML, or dictionary sources.
    
    This class provides a centralized way to load, validate, and access
    pipeline configurations for different flows.
    """
    
    def __init__(self):
        self.configs: Dict[str, Dict[str, Any]] = {}
    
    def load_from_file(self, config_path: str, flow_name: Optional[str] = None) -> Dict[str, Any]:
        """
        Load configuration from a JSON or YAML file.
        
        Args:
            config_path: Path to the configuration file
            flow_name: Optional flow name to associate with this config
            
        Returns:
            The loaded configuration dictionary
            
        Raises:
            ConfigurationError: If file cannot be loaded or parsed
        """
        path = Path(config_path)
        
        if not path.exists():
            raise ConfigurationError(f"Configuration file not found: {config_path}")
        
        try:
            with open(path, 'r') as f:
                if path.suffix in ['.yaml', '.yml']:
                    config = yaml.safe_load(f)
                elif path.suffix == '.json':
                    config = json.load(f)
                else:
                    raise ConfigurationError(f"Unsupported file format: {path.suffix}")
            
            if flow_name:
                self.configs[flow_name] = config
            
            return config
            
        except Exception as e:
            raise ConfigurationError(f"Error loading configuration: {str(e)}")
    
    def load_from_dict(self, config: Dict[str, Any], flow_name: str) -> Dict[str, Any]:
        """
        Load configuration from a dictionary.
        
        Args:
            config: Configuration dictionary
            flow_name: Flow name to associate with this config
            
        Returns:
            The configuration dictionary
        """
        self.configs[flow_name] = config
        return config
    
    def get_config(self, flow_name: str) -> Dict[str, Any]:
        """
        Get configuration for a specific flow.
        
        Args:
            flow_name: Name of the flow
            
        Returns:
            Configuration dictionary
            
        Raises:
            ConfigurationError: If configuration not found
        """
        if flow_name not in self.configs:
            raise ConfigurationError(f"Configuration not found for flow: {flow_name}")
        return self.configs[flow_name]
    
    def validate_config(self, config: Dict[str, Any]) -> bool:
        """
        Validate that a configuration has required fields.
        
        Args:
            config: Configuration dictionary to validate
            
        Returns:
            True if valid
            
        Raises:
            ConfigurationError: If validation fails
        """
        required_fields = ['flow_name', 'stages']
        
        for field in required_fields:
            if field not in config:
                raise ConfigurationError(f"Missing required field: {field}")
        
        if not isinstance(config['stages'], list):
            raise ConfigurationError("'stages' must be a list")
        
        for idx, stage in enumerate(config['stages']):
            if 'name' not in stage or 'type' not in stage:
                raise ConfigurationError(f"Stage {idx} missing 'name' or 'type'")
        
        return True
    
    def get_all_flows(self) -> List[str]:
        """
        Get list of all configured flow names.
        
        Returns:
            List of flow names
        """
        return list(self.configs.keys())
