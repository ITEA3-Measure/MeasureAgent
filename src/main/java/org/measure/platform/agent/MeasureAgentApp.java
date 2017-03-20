package org.measure.platform.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class MeasureAgentApp {

    public static void main(String[] args) {
        SpringApplication.run(MeasureAgentApp.class, args);
    }
}
