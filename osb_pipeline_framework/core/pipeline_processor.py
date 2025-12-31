"""
Pipeline Processor Module

Core module that processes individual pipeline stages.
"""

from typing import Dict, Any, List, Optional
import logging
from datetime import datetime

from .validator import SchemaValidator, ValidationError
from .transformer import TransformationEngine, TransformationError


class PipelineError(Exception):
    """Raised when pipeline processing fails"""
    pass


class PipelineProcessor:
    """
    Processes data through pipeline stages.
    
    Each stage can include validation, transformation, and custom processing.
    Provides hooks for error handling and logging.
    """
    
    def __init__(self, validator: Optional[SchemaValidator] = None, 
                 transformer: Optional[TransformationEngine] = None):
        """
        Initialize the pipeline processor.
        
        Args:
            validator: Schema validator instance (creates new if None)
            transformer: Transformation engine instance (creates new if None)
        """
        self.validator = validator or SchemaValidator()
        self.transformer = transformer or TransformationEngine()
        self.logger = logging.getLogger(__name__)
        self.stage_handlers: Dict[str, callable] = {}
        self._register_builtin_handlers()
    
    def _register_builtin_handlers(self):
        """Register built-in stage handlers"""
        self.stage_handlers['validation'] = self._handle_validation_stage
        self.stage_handlers['transformation'] = self._handle_transformation_stage
        self.stage_handlers['enrichment'] = self._handle_enrichment_stage
        self.stage_handlers['routing'] = self._handle_routing_stage
    
    def register_stage_handler(self, stage_type: str, handler: callable):
        """
        Register a custom stage handler.
        
        Args:
            stage_type: Type identifier for the stage
            handler: Callable that processes the stage
        """
        self.stage_handlers[stage_type] = handler
    
    def process_stage(self, data: Dict[str, Any], stage_config: Dict[str, Any],
                     context: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        Process a single pipeline stage.
        
        Args:
            data: Input data for the stage
            stage_config: Configuration for the stage
            context: Optional context dictionary for sharing state
            
        Returns:
            Processed data
            
        Raises:
            PipelineError: If stage processing fails
        """
        stage_name = stage_config.get('name', 'unknown')
        stage_type = stage_config.get('type')
        
        self.logger.info(f"Processing stage: {stage_name} (type: {stage_type})")
        
        if not stage_type:
            raise PipelineError(f"Stage '{stage_name}' missing type")
        
        if stage_type not in self.stage_handlers:
            raise PipelineError(f"Unknown stage type: {stage_type}")
        
        try:
            result = self.stage_handlers[stage_type](data, stage_config, context or {})
            self.logger.info(f"Stage '{stage_name}' completed successfully")
            return result
        except Exception as e:
            self.logger.error(f"Stage '{stage_name}' failed: {str(e)}")
            raise PipelineError(f"Stage '{stage_name}' failed: {str(e)}")
    
    def process_pipeline(self, data: Dict[str, Any], stages: List[Dict[str, Any]],
                        context: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        Process data through multiple pipeline stages.
        
        Args:
            data: Input data
            stages: List of stage configurations
            context: Optional context dictionary
            
        Returns:
            Final processed data
            
        Raises:
            PipelineError: If any stage fails
        """
        result = data
        ctx = context or {}
        
        ctx['pipeline_start_time'] = datetime.now().isoformat()
        ctx['stages_completed'] = []
        
        for stage in stages:
            stage_name = stage.get('name', 'unknown')
            try:
                result = self.process_stage(result, stage, ctx)
                ctx['stages_completed'].append(stage_name)
            except PipelineError:
                ctx['failed_stage'] = stage_name
                raise
        
        ctx['pipeline_end_time'] = datetime.now().isoformat()
        return result
    
    # Built-in stage handlers
    
    def _handle_validation_stage(self, data: Dict[str, Any], stage_config: Dict[str, Any],
                                 context: Dict[str, Any]) -> Dict[str, Any]:
        """Handle validation stage"""
        schema_name = stage_config.get('schema')
        inline_schema = stage_config.get('inline_schema')
        
        if schema_name:
            self.validator.validate(data, schema_name)
        elif inline_schema:
            self.validator.validate_with_schema(data, inline_schema)
        else:
            raise PipelineError("Validation stage requires 'schema' or 'inline_schema'")
        
        return data
    
    def _handle_transformation_stage(self, data: Dict[str, Any], stage_config: Dict[str, Any],
                                     context: Dict[str, Any]) -> Dict[str, Any]:
        """Handle transformation stage"""
        transformations = stage_config.get('transformations', [])
        
        if not transformations:
            raise PipelineError("Transformation stage requires 'transformations' list")
        
        return self.transformer.apply_transformations(data, transformations)
    
    def _handle_enrichment_stage(self, data: Dict[str, Any], stage_config: Dict[str, Any],
                                context: Dict[str, Any]) -> Dict[str, Any]:
        """Handle enrichment stage"""
        enrichment_data = stage_config.get('enrichment_data', {})
        merge_strategy = stage_config.get('merge_strategy', 'update')
        
        if merge_strategy == 'update':
            data.update(enrichment_data)
        elif merge_strategy == 'append':
            for key, value in enrichment_data.items():
                if key not in data:
                    data[key] = value
        
        return data
    
    def _handle_routing_stage(self, data: Dict[str, Any], stage_config: Dict[str, Any],
                             context: Dict[str, Any]) -> Dict[str, Any]:
        """Handle routing stage - determine next steps based on data"""
        routing_key = stage_config.get('routing_key')
        
        if routing_key and routing_key in data:
            context['routing_decision'] = data[routing_key]
        
        return data
