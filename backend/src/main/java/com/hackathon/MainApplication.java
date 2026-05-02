package com.hackathon;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Flight Dashboard with auto-fix capabilities.
 * This application monitors flight-related issues and provides automated fixes.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MainApplication {

    public static void main(String[] args) {
        // Load .env file before starting Spring Boot
        try {
            Dotenv dotenv = Dotenv.configure()
                .directory("./backend")
                .ignoreIfMissing()
                .load();
            
            // Set environment variables as system properties so Spring can access them
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
                log("Loaded environment variable: " + entry.getKey());
            });
            
            log("Successfully loaded .env file with " + dotenv.entries().size() + " variables");
        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
            System.err.println("Application will use default/mock values");
        }
        
        SpringApplication.run(MainApplication.class, args);
    }
    
    private static void log(String message) {
        System.out.println("[ENV-LOADER] " + message);
    }
}

// Made with Bob
