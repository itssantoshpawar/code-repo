package com.osb.pipeline.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;

@Slf4j
@Service
public class SchemaValidationService {
    
    public void validateAgainstSchema(String xmlPayload, String schemaPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            
            // Try to load schema from classpath
            ClassPathResource schemaResource = new ClassPathResource(schemaPath);
            Schema schema;
            
            if (schemaResource.exists()) {
                schema = factory.newSchema(schemaResource.getURL());
            } else {
                log.warn("Schema not found at path: {}, skipping schema validation", schemaPath);
                return;
            }
            
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlPayload)));
            
            log.debug("Schema validation successful for schema: {}", schemaPath);
        } catch (SAXException e) {
            throw new IllegalArgumentException("Schema validation failed: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading schema or payload: " + e.getMessage(), e);
        }
    }
}
