package com.osb.pipeline;

import com.osb.pipeline.service.FlowOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Example runner demonstrating the OSB Pipeline Framework
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExampleRunner implements CommandLineRunner {
    
    private final FlowOrchestrator flowOrchestrator;
    
    @Override
    public void run(String... args) {
        log.info("=".repeat(60));
        log.info("OSB Pipeline Framework - Spring Boot Examples");
        log.info("=".repeat(60));
        
        // Run examples if --run-examples flag is provided
        if (args.length > 0 && "--run-examples".equals(args[0])) {
            runCustomerFlowExample();
            runPaymentFlowExample();
            
            log.info("=".repeat(60));
            log.info("All examples completed!");
            log.info("=".repeat(60));
        } else {
            log.info("Application started. Use --run-examples to run demo flows.");
            log.info("=".repeat(60));
        }
    }
    
    private void runCustomerFlowExample() {
        log.info("\nExample 1: Customer Data Processing Flow");
        log.info("-".repeat(60));
        
        try {
            // Register flow
            flowOrchestrator.registerFlowFromFile(
                "customer_data_processing", 
                "src/main/resources/config/customer-flow.json"
            );
            
            // Input data
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("customer_id", "CUST001");
            customerData.put("name", "john doe");
            customerData.put("email", "JOHN.DOE@EXAMPLE.COM");
            customerData.put("age", 35);
            
            log.info("Input: {}", customerData);
            
            // Execute flow
            Map<String, Object> result = flowOrchestrator.executeFlow(
                "customer_data_processing", 
                customerData
            );
            
            log.info("Output: {}", result);
            log.info("✓ Customer flow completed successfully!");
            
        } catch (Exception e) {
            log.error("✗ Customer flow failed: {}", e.getMessage());
        }
    }
    
    private void runPaymentFlowExample() {
        log.info("\nExample 2: Payment Processing Flow");
        log.info("-".repeat(60));
        
        try {
            // Register flow
            flowOrchestrator.registerFlowFromFile(
                "payment_processing", 
                "src/main/resources/config/payment-flow.json"
            );
            
            // Input data with non-standard field names
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("txn_id", "TXN789");
            paymentData.put("amt", 299.99);
            paymentData.put("curr", "USD");
            paymentData.put("cust_id", "CUST001");
            paymentData.put("extra_field", "will_be_removed");
            
            log.info("Input (non-standard fields): {}", paymentData);
            
            // Execute flow
            Map<String, Object> result = flowOrchestrator.executeFlow(
                "payment_processing", 
                paymentData
            );
            
            log.info("Output (standard fields): {}", result);
            log.info("✓ Payment flow completed successfully!");
            
        } catch (Exception e) {
            log.error("✗ Payment flow failed: {}", e.getMessage());
        }
    }
}
