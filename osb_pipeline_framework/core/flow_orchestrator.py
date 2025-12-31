"""
Flow Orchestrator Module

Orchestrates multiple flows and manages their execution.
"""

from typing import Dict, Any, List, Optional
import logging
from datetime import datetime

from .pipeline_processor import PipelineProcessor, PipelineError
from .validator import SchemaValidator
from .transformer import TransformationEngine
from ..config.configuration_manager import ConfigurationManager, ConfigurationError


class FlowOrchestrator:
    """
    Orchestrates execution of multiple pipeline flows.
    
    Manages flow lifecycle, error handling, and coordination between flows.
    """
    
    def __init__(self, config_manager: Optional[ConfigurationManager] = None):
        """
        Initialize the flow orchestrator.
        
        Args:
            config_manager: Configuration manager instance (creates new if None)
        """
        self.config_manager = config_manager or ConfigurationManager()
        self.validator = SchemaValidator()
        self.transformer = TransformationEngine()
        self.processor = PipelineProcessor(self.validator, self.transformer)
        self.logger = logging.getLogger(__name__)
        
        self.flow_results: Dict[str, Any] = {}
        self.flow_errors: Dict[str, str] = {}
    
    def register_flow(self, flow_name: str, flow_config: Dict[str, Any]):
        """
        Register a flow with its configuration.
        
        Args:
            flow_name: Name of the flow
            flow_config: Flow configuration dictionary
        """
        self.config_manager.load_from_dict(flow_config, flow_name)
        self.logger.info(f"Registered flow: {flow_name}")
    
    def register_flow_from_file(self, flow_name: str, config_path: str):
        """
        Register a flow from a configuration file.
        
        Args:
            flow_name: Name of the flow
            config_path: Path to configuration file
        """
        self.config_manager.load_from_file(config_path, flow_name)
        self.logger.info(f"Registered flow '{flow_name}' from {config_path}")
    
    def execute_flow(self, flow_name: str, input_data: Dict[str, Any],
                    context: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        Execute a single flow.
        
        Args:
            flow_name: Name of the flow to execute
            input_data: Input data for the flow
            context: Optional context dictionary
            
        Returns:
            Flow execution result
            
        Raises:
            FlowExecutionError: If flow execution fails
        """
        self.logger.info(f"Starting flow execution: {flow_name}")
        
        try:
            # Get flow configuration
            config = self.config_manager.get_config(flow_name)
            self.config_manager.validate_config(config)
            
            # Set up context
            ctx = context or {}
            ctx['flow_name'] = flow_name
            ctx['execution_start'] = datetime.now().isoformat()
            
            # Execute pipeline stages
            stages = config.get('stages', [])
            result = self.processor.process_pipeline(input_data, stages, ctx)
            
            ctx['execution_end'] = datetime.now().isoformat()
            ctx['status'] = 'success'
            
            # Store result
            self.flow_results[flow_name] = {
                'result': result,
                'context': ctx
            }
            
            self.logger.info(f"Flow '{flow_name}' completed successfully")
            return result
            
        except (ConfigurationError, PipelineError) as e:
            self.logger.error(f"Flow '{flow_name}' failed: {str(e)}")
            self.flow_errors[flow_name] = str(e)
            raise FlowExecutionError(f"Flow '{flow_name}' failed: {str(e)}")
        except Exception as e:
            self.logger.error(f"Unexpected error in flow '{flow_name}': {str(e)}")
            self.flow_errors[flow_name] = str(e)
            raise FlowExecutionError(f"Unexpected error in flow '{flow_name}': {str(e)}")
    
    def execute_multiple_flows(self, flow_configs: List[Dict[str, Any]],
                              shared_context: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        Execute multiple flows sequentially or in parallel.
        
        Args:
            flow_configs: List of flow execution configurations
                Each should have: {'flow_name': str, 'input_data': dict}
            shared_context: Optional shared context for all flows
            
        Returns:
            Dictionary with results from all flows
        """
        results = {}
        ctx = shared_context or {}
        
        for flow_config in flow_configs:
            flow_name = flow_config.get('flow_name')
            input_data = flow_config.get('input_data', {})
            
            if not flow_name:
                self.logger.warning("Skipping flow config without flow_name")
                continue
            
            try:
                result = self.execute_flow(flow_name, input_data, ctx.copy())
                results[flow_name] = {
                    'status': 'success',
                    'result': result
                }
            except FlowExecutionError as e:
                results[flow_name] = {
                    'status': 'failed',
                    'error': str(e)
                }
        
        return results
    
    def get_flow_result(self, flow_name: str) -> Optional[Dict[str, Any]]:
        """
        Get the result of a previously executed flow.
        
        Args:
            flow_name: Name of the flow
            
        Returns:
            Flow result or None if not found
        """
        return self.flow_results.get(flow_name)
    
    def get_flow_error(self, flow_name: str) -> Optional[str]:
        """
        Get the error message for a failed flow.
        
        Args:
            flow_name: Name of the flow
            
        Returns:
            Error message or None if no error
        """
        return self.flow_errors.get(flow_name)
    
    def clear_results(self):
        """Clear all stored flow results and errors"""
        self.flow_results.clear()
        self.flow_errors.clear()


class FlowExecutionError(Exception):
    """Raised when flow execution fails"""
    pass
