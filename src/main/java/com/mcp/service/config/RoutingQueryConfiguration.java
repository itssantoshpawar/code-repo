package com.mcp.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "routing.query")
public class RoutingQueryConfiguration {
    
    private Map<String, String> queries = new HashMap<>();
    
    public RoutingQueryConfiguration() {
        initializeDefaultQueries();
    }
    
    private void initializeDefaultQueries() {
        queries.put("t2r", "SELECT * FROM routing_table WHERE flow_type = 't2r'");
        queries.put("b2b", "SELECT * FROM routing_table WHERE flow_type = 'b2b'");
        queries.put("siebel", "SELECT * FROM routing_table WHERE flow_type = 'siebel'");
        queries.put("portal", "SELECT * FROM routing_table WHERE flow_type = 'portal'");
        queries.put("flow", "SELECT * FROM routing_table WHERE flow_type = 'flow'");
        queries.put("neo", "SELECT * FROM routing_table WHERE flow_type = 'neo'");
        queries.put("sync", "SELECT * FROM routing_table WHERE flow_type = 'sync'");
    }
    
    public String getQuery(String flowType) {
        return queries.getOrDefault(flowType, "SELECT * FROM routing_table WHERE flow_type = 'default'");
    }
}
