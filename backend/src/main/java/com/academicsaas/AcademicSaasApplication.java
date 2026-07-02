package com.academicsaas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AcademicSaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicSaasApplication.class, args);
    }
}
