package com.hackathon.model;

/**
 * Record representing the result of a Playwright test execution.
 * 
 * @param passed whether the test passed
 * @param testName name of the test
 * @param output test output/logs
 * @param duration test duration in milliseconds
 * @param timestamp ISO format timestamp
 */
public record TestResult(
    boolean passed,
    String testName,
    String output,
    int duration,
    String timestamp
) {
}

// Made with Bob
