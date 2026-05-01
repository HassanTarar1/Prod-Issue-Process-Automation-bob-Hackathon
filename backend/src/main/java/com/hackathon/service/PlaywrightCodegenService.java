package com.hackathon.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Service for generating Playwright test code using codegen.
 */
@Service
@Slf4j
public class PlaywrightCodegenService {

    private static final String TESTS_DIR = "tests";
    private static final int CODEGEN_TIMEOUT_SECONDS = 30;

    /**
     * Generates a Playwright test by recording user interactions.
     * 
     * @param url URL to record (default: http://localhost:4200)
     * @return path to the generated test file
     */
    public String generateTest(String url) {
        if (url == null || url.isEmpty()) {
            url = "http://localhost:4200";
        }

        try {
            // Create tests directory if not exists
            File testsDir = new File(TESTS_DIR);
            if (!testsDir.exists()) {
                boolean created = testsDir.mkdirs();
                if (created) {
                    log.info("Created tests directory: {}", TESTS_DIR);
                }
            }

            // Generate output filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String filename = String.format("%s/recorded-test-%s.spec.ts", TESTS_DIR, timestamp);

            // Build command
            String command = String.format("npx playwright codegen %s --output %s", url, filename);
            log.info("Executing Playwright codegen command: {}", command);

            // Execute command using ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // Handle Windows vs Linux command execution
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                processBuilder.command("sh", "-c", command);
            }

            processBuilder.directory(new File(System.getProperty("user.dir")));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Capture output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.debug("Codegen output: {}", line);
                }
            }

            // Wait for process to complete with timeout
            boolean completed = process.waitFor(CODEGEN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                log.error("Playwright codegen timed out after {} seconds", CODEGEN_TIMEOUT_SECONDS);
                return "Error: Command timed out";
            }

            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("Playwright codegen completed successfully. Test file: {}", filename);
                return filename;
            } else {
                log.error("Playwright codegen failed with exit code: {}. Output: {}", exitCode, output);
                return "Error: Command failed with exit code " + exitCode;
            }

        } catch (IOException e) {
            log.error("IOException while executing Playwright codegen", e);
            return "Error: " + e.getMessage();
        } catch (InterruptedException e) {
            log.error("Interrupted while executing Playwright codegen", e);
            Thread.currentThread().interrupt();
            return "Error: Process interrupted";
        } catch (Exception e) {
            log.error("Unexpected error during Playwright codegen", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Checks if Playwright is installed.
     * 
     * @return true if Playwright is installed, false otherwise
     */
    public boolean isPlaywrightInstalled() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // Handle Windows vs Linux command execution
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                processBuilder.command("cmd.exe", "/c", "npx playwright --version");
            } else {
                processBuilder.command("sh", "-c", "npx playwright --version");
            }

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Wait for process to complete with short timeout
            boolean completed = process.waitFor(5, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                log.warn("Playwright version check timed out");
                return false;
            }

            int exitCode = process.exitValue();
            boolean installed = exitCode == 0;
            
            if (installed) {
                log.info("Playwright is installed");
            } else {
                log.warn("Playwright is not installed or not accessible");
            }
            
            return installed;

        } catch (IOException e) {
            log.error("IOException while checking Playwright installation", e);
            return false;
        } catch (InterruptedException e) {
            log.error("Interrupted while checking Playwright installation", e);
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while checking Playwright installation", e);
            return false;
        }
    }
}

// Made with Bob
