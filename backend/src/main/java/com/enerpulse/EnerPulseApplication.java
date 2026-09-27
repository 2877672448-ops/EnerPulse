package com.enerpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnerPulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnerPulseApplication.class, args);
    }
}
