package com.ecfr.config;

import com.ecfr.script.ProcessTitle43Xml;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessTitle43XmlRunner {
    
    @Bean
    public CommandLineRunner processTitle43XmlRunner(ProcessTitle43Xml processor) {
        return args -> {
            processor.processXmlFile();
        };
    }
} 