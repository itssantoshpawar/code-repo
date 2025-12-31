"""
Example usage of the OSB Pipeline Framework

This script demonstrates how to use the framework to process
different types of data flows.

To run this example:
1. Set PYTHONPATH: export PYTHONPATH=/path/to/code-repo:$PYTHONPATH
2. Or install the package: pip install -e /path/to/code-repo
3. Run: python usage_examples.py
"""

import sys
import json
from pathlib import Path

# For examples only - in production, install the package properly
sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from osb_pipeline_framework.core import (
    FlowOrchestrator,
    PipelineProcessor,
    SchemaValidator,
    TransformationEngine
)
from osb_pipeline_framework.config import ConfigurationManager
from osb_pipeline_framework.utils import setup_logger


def example_customer_flow():
    """Example: Process customer data"""
    print("\n" + "="*60)
    print("Example 1: Customer Data Processing Flow")
    print("="*60 + "\n")
    
    # Set up logger
    logger = setup_logger('customer_flow')
    
    # Initialize orchestrator
    orchestrator = FlowOrchestrator()
    
    # Register flow from config file
    config_path = Path(__file__).parent / 'customer_flow_config.json'
    orchestrator.register_flow_from_file('customer_data_processing', str(config_path))
    
    # Sample input data
    customer_data = {
        'customer_id': 'CUST001',
        'name': 'john doe',
        'email': 'JOHN.DOE@EXAMPLE.COM',
        'age': 35
    }
    
    print(f"Input data: {json.dumps(customer_data, indent=2)}\n")
    
    # Execute flow
    try:
        result = orchestrator.execute_flow('customer_data_processing', customer_data)
        print(f"Output data: {json.dumps(result, indent=2)}\n")
        print("✓ Customer flow completed successfully!")
    except Exception as e:
        print(f"✗ Flow failed: {str(e)}")


def example_order_flow():
    """Example: Process order data"""
    print("\n" + "="*60)
    print("Example 2: Order Processing Flow")
    print("="*60 + "\n")
    
    # Set up logger
    logger = setup_logger('order_flow')
    
    # Initialize orchestrator
    orchestrator = FlowOrchestrator()
    
    # Register flow from config file
    config_path = Path(__file__).parent / 'order_flow_config.json'
    orchestrator.register_flow_from_file('order_processing', str(config_path))
    
    # Sample input data
    order_data = {
        'order_id': 'ord123',
        'customer_id': 'CUST001',
        'items': [
            {'item_id': 'ITEM001', 'quantity': 2, 'price': 29.99},
            {'item_id': 'ITEM002', 'quantity': 1, 'price': 49.99}
        ],
        'total_amount': 109.97,
        'currency': 'USD'
    }
    
    print(f"Input data: {json.dumps(order_data, indent=2)}\n")
    
    # Execute flow
    try:
        result = orchestrator.execute_flow('order_processing', order_data)
        print(f"Output data: {json.dumps(result, indent=2)}\n")
        print("✓ Order flow completed successfully!")
    except Exception as e:
        print(f"✗ Flow failed: {str(e)}")


def example_payment_flow():
    """Example: Process payment data with field mapping"""
    print("\n" + "="*60)
    print("Example 3: Payment Processing Flow (Field Mapping)")
    print("="*60 + "\n")
    
    # Set up logger
    logger = setup_logger('payment_flow')
    
    # Initialize orchestrator
    orchestrator = FlowOrchestrator()
    
    # Register flow from config file
    config_path = Path(__file__).parent / 'payment_flow_config.json'
    orchestrator.register_flow_from_file('payment_processing', str(config_path))
    
    # Sample input data with non-standard field names
    payment_data = {
        'txn_id': 'TXN789',
        'amt': 299.99,
        'curr': 'USD',
        'cust_id': 'CUST001',
        'extra_field': 'will_be_removed'
    }
    
    print(f"Input data (non-standard fields): {json.dumps(payment_data, indent=2)}\n")
    
    # Execute flow
    try:
        result = orchestrator.execute_flow('payment_processing', payment_data)
        print(f"Output data (standard fields): {json.dumps(result, indent=2)}\n")
        print("✓ Payment flow completed successfully!")
    except Exception as e:
        print(f"✗ Flow failed: {str(e)}")


def example_custom_transformation():
    """Example: Custom transformation logic"""
    print("\n" + "="*60)
    print("Example 4: Custom Transformation")
    print("="*60 + "\n")
    
    # Initialize transformation engine
    transformer = TransformationEngine()
    
    # Register a custom transformation
    def calculate_discount(data, params):
        """Custom transformation to calculate discount"""
        discount_rate = params.get('discount_rate', 0.1)
        if 'total_amount' in data:
            data['discount'] = data['total_amount'] * discount_rate
            data['final_amount'] = data['total_amount'] - data['discount']
        return data
    
    transformer.register_transformation('calculate_discount', calculate_discount)
    
    # Sample data
    order_data = {
        'order_id': 'ORD456',
        'total_amount': 100.0
    }
    
    print(f"Input data: {json.dumps(order_data, indent=2)}\n")
    
    # Apply custom transformation
    transformation_config = {
        'type': 'calculate_discount',
        'params': {
            'discount_rate': 0.15
        }
    }
    
    result = transformer.transform(order_data, transformation_config)
    print(f"Output data: {json.dumps(result, indent=2)}\n")
    print("✓ Custom transformation completed successfully!")


def example_multiple_flows():
    """Example: Execute multiple flows"""
    print("\n" + "="*60)
    print("Example 5: Multiple Flows Execution")
    print("="*60 + "\n")
    
    # Initialize orchestrator
    orchestrator = FlowOrchestrator()
    
    # Register all flows
    config_dir = Path(__file__).parent
    orchestrator.register_flow_from_file('customer_data_processing', 
                                        str(config_dir / 'customer_flow_config.json'))
    orchestrator.register_flow_from_file('payment_processing', 
                                        str(config_dir / 'payment_flow_config.json'))
    
    # Define multiple flow executions
    flow_configs = [
        {
            'flow_name': 'customer_data_processing',
            'input_data': {
                'customer_id': 'CUST002',
                'name': 'jane smith',
                'email': 'JANE@EXAMPLE.COM',
                'age': 28
            }
        },
        {
            'flow_name': 'payment_processing',
            'input_data': {
                'txn_id': 'TXN999',
                'amt': 150.00,
                'curr': 'EUR',
                'cust_id': 'CUST002'
            }
        }
    ]
    
    print("Executing multiple flows...\n")
    
    # Execute all flows
    results = orchestrator.execute_multiple_flows(flow_configs)
    
    # Display results
    for flow_name, result in results.items():
        print(f"\nFlow: {flow_name}")
        print(f"Status: {result['status']}")
        if result['status'] == 'success':
            print(f"Result: {json.dumps(result['result'], indent=2)}")
        else:
            print(f"Error: {result['error']}")
    
    print("\n✓ Multiple flows execution completed!")


if __name__ == '__main__':
    print("\n" + "="*60)
    print("OSB Pipeline Framework - Usage Examples")
    print("="*60)
    
    # Run all examples
    example_customer_flow()
    example_order_flow()
    example_payment_flow()
    example_custom_transformation()
    example_multiple_flows()
    
    print("\n" + "="*60)
    print("All examples completed!")
    print("="*60 + "\n")
