package com.osb.pipeline.service;

import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

@Slf4j
@Service
public class XQueryTransformationService {
    
    private final Processor processor;
    
    public XQueryTransformationService() {
        this.processor = new Processor(false);
    }
    
    public String transform(String xmlPayload, String xqueryPath) {
        try {
            XQueryCompiler compiler = processor.newXQueryCompiler();
            
            // Try to load XQuery from classpath
            ClassPathResource xqueryResource = new ClassPathResource(xqueryPath);
            XQueryExecutable executable;
            
            if (xqueryResource.exists()) {
                executable = compiler.compile(xqueryResource.getInputStream());
            } else {
                log.warn("XQuery not found at path: {}, returning original payload", xqueryPath);
                return xmlPayload;
            }
            
            XQueryEvaluator evaluator = executable.load();
            evaluator.setSource(new StreamSource(new StringReader(xmlPayload)));
            
            XdmValue result = evaluator.evaluate();
            String transformedPayload = result.toString();
            
            log.debug("XQuery transformation successful for: {}", xqueryPath);
            return transformedPayload;
            
        } catch (SaxonApiException e) {
            throw new RuntimeException("XQuery transformation failed: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading XQuery file: " + e.getMessage(), e);
        }
    }
}
