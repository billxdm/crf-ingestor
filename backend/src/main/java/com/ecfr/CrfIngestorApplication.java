package com.ecfr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.retry.annotation.EnableRetry;
import com.ecfr.config.ProcessTitle43XmlRunner;

@SpringBootApplication
@EnableRetry
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProcessTitle43XmlRunner.class))
public class CrfIngestorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrfIngestorApplication.class, args);
    }
} 