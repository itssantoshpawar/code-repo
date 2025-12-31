"""
Generic OSB Pipeline Processing Framework

A flexible, configuration-driven framework for processing OSB pipelines
across multiple flows with support for transformations, schema validation,
and error handling.
"""

__version__ = "1.0.0"
__author__ = "OSB Pipeline Framework Team"

from .core.pipeline_processor import PipelineProcessor
from .core.flow_orchestrator import FlowOrchestrator
from .core.transformer import TransformationEngine
from .core.validator import SchemaValidator

__all__ = [
    "PipelineProcessor",
    "FlowOrchestrator",
    "TransformationEngine",
    "SchemaValidator",
]
