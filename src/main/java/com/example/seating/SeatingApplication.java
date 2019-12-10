package com.example.seating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Roy
 */
@EnableAsync
@SpringBootApplication
public class SeatingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeatingApplication.class,args);
    }
}
