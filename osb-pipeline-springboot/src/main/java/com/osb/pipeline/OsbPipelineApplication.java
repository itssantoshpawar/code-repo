package com.osb.pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OSB Pipeline Framework Application
 * 
 * A flexible, configuration-driven framework for processing OSB pipelines
 * across multiple flows with support for transformations, schema validation,
 * and error handling.
 */
@SpringBootApplication
public class OsbPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsbPipelineApplication.class, args);
    }
}
