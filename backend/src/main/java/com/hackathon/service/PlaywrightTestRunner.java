package com.hackathon.service;

import com.hackathon.model.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for running Playwright tests and parsing results.
 */
@Service
@Slf4j
public class PlaywrightTestRunner {

    private static final int TEST_TIMEOUT_SECONDS = 60;
    private static final String TESTS_DIR = "tests";

    /**
     * Runs a specific Playwright test file.
     * 
     * @param testFilePath path to the test file to run
     * @return TestResult containing test execution details
     */
    public TestResult runTest(String testFilePath) {
        long startTime = System.currentTimeMillis();
        String timestamp = Instant.now().toString();

        try {
            // Validate test file exists
            File testFile = new File(testFilePath);
            if (!testFile.exists()) {
                log.error("Test file not found: {}", testFilePath);
                return new TestResult(
                    false,
                    testFilePath,
                    "Error: Test file not found",
                    0,
                    timestamp
                );
            }

            // Build command
            String command = String.format("npx playwright test %s", testFilePath);
            log.info("Executing Playwright test command: {}", command);

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
                    log.debug("Test output: {}", line);
                }
            }

            // Wait for process to complete with timeout
            boolean completed = process.waitFor(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                long duration = System.currentTimeMillis() - startTime;
                log.error("Playwright test timed out after {} seconds", TEST_TIMEOUT_SECONDS);
                return new TestResult(
                    false,
                    testFilePath,
                    "Error: Test execution timed out after " + TEST_TIMEOUT_SECONDS + " seconds",
                    (int) duration,
                    timestamp
                );
            }

            int exitCode = process.exitValue();
            long duration = System.currentTimeMillis() - startTime;
            String outputStr = output.toString();

            // Parse output to determine if test passed
            boolean passed = exitCode == 0;
            
            log.info("Playwright test completed. Exit code: {}, Duration: {}ms, Passed: {}", 
                     exitCode, duration, passed);

            return new TestResult(
                passed,
                testFilePath,
                outputStr,
                (int) duration,
                timestamp
            );

        } catch (IOException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("IOException while executing Playwright test", e);
            return new TestResult(
                false,
                testFilePath,
                "Error: " + e.getMessage(),
                (int) duration,
                timestamp
            );
        } catch (InterruptedException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Interrupted while executing Playwright test", e);
            Thread.currentThread().interrupt();
            return new TestResult(
                false,
                testFilePath,
                "Error: Process interrupted",
                (int) duration,
                timestamp
            );
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Unexpected error during Playwright test execution", e);
            return new TestResult(
                false,
                testFilePath,
                "Error: " + e.getMessage(),
                (int) duration,
                timestamp
            );
        }
    }

    /**
     * Runs all Playwright tests in the tests/ directory.
     * 
     * @return List of TestResult objects for all tests
     */
    public List<TestResult> runAllTests() {
        long startTime = System.currentTimeMillis();
        String timestamp = Instant.now().toString();
        List<TestResult> results = new ArrayList<>();

        try {
            // Check if tests directory exists
            File testsDir = new File(TESTS_DIR);
            if (!testsDir.exists() || !testsDir.isDirectory()) {
                log.warn("Tests directory not found: {}", TESTS_DIR);
                results.add(new TestResult(
                    false,
                    "all-tests",
                    "Error: Tests directory not found",
                    0,
                    timestamp
                ));
                return results;
            }

            // Build command
            String command = "npx playwright test";
            log.info("Executing Playwright test command: {}", command);

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
                    log.debug("Test output: {}", line);
                }
            }

            // Wait for process to complete with timeout
            boolean completed = process.waitFor(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                long duration = System.currentTimeMillis() - startTime;
                log.error("Playwright tests timed out after {} seconds", TEST_TIMEOUT_SECONDS);
                results.add(new TestResult(
                    false,
                    "all-tests",
                    "Error: Test execution timed out after " + TEST_TIMEOUT_SECONDS + " seconds",
                    (int) duration,
                    timestamp
                ));
                return results;
            }

            int exitCode = process.exitValue();
            long duration = System.currentTimeMillis() - startTime;
            String outputStr = output.toString();

            // Parse output for multiple test results
            List<TestResult> parsedResults = parseTestOutput(outputStr);
            
            if (parsedResults.isEmpty()) {
                // If no tests were parsed, return a single result
                boolean passed = exitCode == 0;
                results.add(new TestResult(
                    passed,
                    "all-tests",
                    outputStr,
                    (int) duration,
                    timestamp
                ));
            } else {
                results.addAll(parsedResults);
            }

            log.info("Playwright tests completed. Exit code: {}, Duration: {}ms, Tests: {}", 
                     exitCode, duration, results.size());

            return results;

        } catch (IOException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("IOException while executing Playwright tests", e);
            results.add(new TestResult(
                false,
                "all-tests",
                "Error: " + e.getMessage(),
                (int) duration,
                timestamp
            ));
            return results;
        } catch (InterruptedException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Interrupted while executing Playwright tests", e);
            Thread.currentThread().interrupt();
            results.add(new TestResult(
                false,
                "all-tests",
                "Error: Process interrupted",
                (int) duration,
                timestamp
            ));
            return results;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Unexpected error during Playwright tests execution", e);
            results.add(new TestResult(
                false,
                "all-tests",
                "Error: " + e.getMessage(),
                (int) duration,
                timestamp
            ));
            return results;
        }
    }

    /**
     * Parses Playwright test output to extract individual test results.
     * 
     * @param output the raw test output
     * @return List of TestResult objects parsed from the output
     */
    private List<TestResult> parseTestOutput(String output) {
        List<TestResult> results = new ArrayList<>();
        String timestamp = Instant.now().toString();

        try {
            // Pattern to match test results in Playwright output
            // Example: "✓ [chromium] › test.spec.ts:3:1 › test name (1.2s)"
            // Example: "✗ [chromium] › test.spec.ts:3:1 › test name (1.2s)"
            Pattern testPattern = Pattern.compile("([✓✗×])\\s+\\[.*?\\]\\s+›\\s+(.*?)\\s+›\\s+(.*?)\\s+\\((\\d+(?:\\.\\d+)?)(ms|s)\\)");
            Matcher matcher = testPattern.matcher(output);

            while (matcher.find()) {
                String status = matcher.group(1);
                String testFile = matcher.group(2);
                String testName = matcher.group(3);
                String durationStr = matcher.group(4);
                String unit = matcher.group(5);

                // Convert duration to milliseconds
                int duration;
                if ("s".equals(unit)) {
                    duration = (int) (Double.parseDouble(durationStr) * 1000);
                } else {
                    duration = (int) Double.parseDouble(durationStr);
                }

                boolean passed = "✓".equals(status);
                String fullTestName = testFile + " › " + testName;

                results.add(new TestResult(
                    passed,
                    fullTestName,
                    output,
                    duration,
                    timestamp
                ));

                log.debug("Parsed test result: {} - {} ({}ms)", passed ? "PASSED" : "FAILED", fullTestName, duration);
            }

            // Alternative pattern for simpler output format
            if (results.isEmpty()) {
                Pattern simplePattern = Pattern.compile("(\\d+)\\s+passed.*?(\\d+)\\s+failed");
                Matcher simpleMatcher = simplePattern.matcher(output);
                
                if (simpleMatcher.find()) {
                    int passed = Integer.parseInt(simpleMatcher.group(1));
                    int failed = Integer.parseInt(simpleMatcher.group(2));
                    
                    results.add(new TestResult(
                        failed == 0,
                        String.format("Test Suite (%d passed, %d failed)", passed, failed),
                        output,
                        0,
                        timestamp
                    ));
                }
            }

        } catch (Exception e) {
            log.error("Error parsing test output", e);
        }

        return results;
    }
}

// Made with Bob
