"""
Error handling utilities for OSB Pipeline Framework
"""

from typing import Dict, Any, Optional, Callable
import traceback
from datetime import datetime


class ErrorHandler:
    """
    Centralized error handling for pipeline processing.
    """
    
    def __init__(self):
        self.error_handlers: Dict[str, Callable[[Exception, Dict[str, Any]], None]] = {}
        self.error_log: list = []
    
    def register_error_handler(self, error_type: str, handler: Callable[[Exception, Dict[str, Any]], None]):
        """
        Register a custom error handler for specific error types.
        
        Args:
            error_type: Type of error to handle
            handler: Callable that handles the error, takes (error, error_info) and returns None
        """
        self.error_handlers[error_type] = handler
    
    def handle_error(self, error: Exception, context: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        Handle an error with appropriate error handler.
        
        Args:
            error: The exception that occurred
            context: Optional context information
            
        Returns:
            Error information dictionary
        """
        error_type = type(error).__name__
        error_info = {
            'error_type': error_type,
            'error_message': str(error),
            'timestamp': datetime.now().isoformat(),
            'context': context or {},
            'traceback': traceback.format_exc()
        }
        
        # Log the error
        self.error_log.append(error_info)
        
        # Call custom handler if registered
        if error_type in self.error_handlers:
            try:
                self.error_handlers[error_type](error, error_info)
            except Exception as handler_error:
                error_info['handler_error'] = str(handler_error)
        
        return error_info
    
    def get_error_log(self) -> list:
        """
        Get all logged errors.
        
        Returns:
            List of error information dictionaries
        """
        return self.error_log
    
    def clear_error_log(self):
        """Clear the error log"""
        self.error_log.clear()
