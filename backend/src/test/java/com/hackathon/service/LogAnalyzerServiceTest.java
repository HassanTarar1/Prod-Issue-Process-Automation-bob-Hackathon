package com.hackathon.service;

import com.hackathon.model.AnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LogAnalyzerService Tests")
class LogAnalyzerServiceTest {

    private LogAnalyzerService logAnalyzerService;

    @BeforeEach
    void setUp() {
        logAnalyzerService = new LogAnalyzerService();
    }

    @Test
    @DisplayName("Should detect NullPointerException")
    void shouldDetectNullPointerException() {
        String logLine = "{\"timestamp\":\"2026-05-01T10:00:00\",\"errorType\":\"NullPointerException\",\"message\":\"Cannot invoke method on null object\",\"stackTrace\":[\"at com.hackathon.service.FlightService.getFlights(FlightService.java:45)\"]}";
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        assertNotNull(result);
        assertEquals("NullPointerException", result.getErrorType());
        assertTrue(result.isFixable());
        assertTrue(result.getMessage().contains("null"));
    }

    @Test
    @DisplayName("Should detect IndexOutOfBoundsException")
    void shouldDetectIndexOutOfBoundsException() {
        String logLine = "{\"timestamp\":\"2026-05-01T10:00:00\",\"errorType\":\"IndexOutOfBoundsException\",\"message\":\"Index 50 out of bounds for length 50\",\"stackTrace\":[\"at com.hackathon.service.FlightService.getFlights(FlightService.java:52)\"]}";
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        assertNotNull(result);
        assertEquals("IndexOutOfBoundsException", result.getErrorType());
        assertTrue(result.isFixable());
        assertTrue(result.getMessage().contains("Index"));
    }

    @Test
    @DisplayName("Should detect ArithmeticException")
    void shouldDetectArithmeticException() {
        String logLine = "{\"timestamp\":\"2026-05-01T10:00:00\",\"errorType\":\"ArithmeticException\",\"message\":\"/ by zero\",\"stackTrace\":[\"at com.hackathon.service.FlightService.getDelayedPercentage(FlightService.java:60)\"]}";
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        assertNotNull(result);
        assertEquals("ArithmeticException", result.getErrorType());
        assertTrue(result.isFixable());
        assertTrue(result.getMessage().contains("zero"));
    }

    @Test
    @DisplayName("Should detect DateTimeParseException")
    void shouldDetectDateTimeParseException() {
        String logLine = "{\"timestamp\":\"2026-05-01T10:00:00\",\"errorType\":\"DateTimeParseException\",\"message\":\"Text '2026-05-01 10:00:00' could not be parsed\",\"stackTrace\":[\"at com.hackathon.service.FlightService.parseFlightDate(FlightService.java:68)\"]}";
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        assertNotNull(result);
        assertEquals("DateTimeParseException", result.getErrorType());
        assertTrue(result.isFixable());
        assertTrue(result.getMessage().contains("parsed"));
    }

    @Test
    @DisplayName("Should return null for non-error log lines")
    void shouldReturnNullForNonErrorLines() {
        String logLine = "INFO: Application started successfully";
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null for empty log lines")
    void shouldReturnNullForEmptyLines() {
        String logLine = "";
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null for null log lines")
    void shouldReturnNullForNullLines() {
        AnalysisResult result = logAnalyzerService.analyzeLogLine(null);
        
        assertNull(result);
    }

    @Test
    @DisplayName("Should extract stack trace from log line")
    void shouldExtractStackTrace() {
        String logLine = "{\"timestamp\":\"2026-05-01T10:00:00\",\"errorType\":\"NullPointerException\",\"message\":\"Cannot invoke method\",\"stackTrace\":[\"at com.hackathon.service.FlightService.getFlights(FlightService.java:45)\",\"at com.hackathon.controller.FlightController.getFlights(FlightController.java:30)\"]}";
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        assertNotNull(result);
        assertNotNull(result.getStackTrace());
        assertTrue(result.getStackTrace().contains("FlightService.java:45"));
    }

    @Test
    @DisplayName("Should mark unknown errors as not fixable")
    void shouldMarkUnknownErrorsAsNotFixable() {
        String logLine = "{\"timestamp\":\"2026-05-01T10:00:00\",\"errorType\":\"UnknownException\",\"message\":\"Something went wrong\",\"stackTrace\":[]}";
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        assertNotNull(result);
        assertEquals("UnknownException", result.getErrorType());
        assertFalse(result.isFixable());
    }

    @Test
    @DisplayName("Should handle malformed JSON gracefully")
    void shouldHandleMalformedJson() {
        String logLine = "{\"timestamp\":\"2026-05-01T10:00:00\",\"errorType\":\"NullPointerException\""; // Missing closing brace
        
        AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
        
        // Should still detect the error type from regex
        assertNotNull(result);
        assertEquals("NullPointerException", result.getErrorType());
    }
}

// Made with Bob
