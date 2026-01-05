package com.osb.pipeline.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "osb")
public class PipelineProperties {
    
    private List<PipelineConfig> pipelines = new ArrayList<>();
    
    @Data
    public static class PipelineConfig {
        private String queueName;
        private String name;
        private boolean validationEnabled;
        private boolean schemaValidationEnabled;
        private String schemaPath;
        private boolean xqueryEnabled;
        private String xqueryPath;
        private RouteType routeType;
        private String routeDestination;
    }
    
    public enum RouteType {
        SERVICE, QUEUE
    }
}
