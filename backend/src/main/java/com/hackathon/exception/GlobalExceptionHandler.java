package com.hackathon.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * Global exception handler for the Flight Dashboard application.
 * Catches all exceptions, logs them as JSON to production.log, and returns appropriate HTTP responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String LOG_FILE = "production.log";

    /**
     * Handles NullPointerException.
     * Returns HTTP 500 with error details.
     */
    @ExceptionHandler(NullPointerException.class)
    public ProblemDetail handleNullPointerException(NullPointerException ex) {
        logErrorAsJson("NullPointerException", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getMessage() != null ? ex.getMessage() : "Null pointer encountered"
        );
        problemDetail.setTitle("Null Pointer Error");
        
        return problemDetail;
    }

    /**
     * Handles IndexOutOfBoundsException.
     * Returns HTTP 400 with error details.
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ProblemDetail handleIndexOutOfBoundsException(IndexOutOfBoundsException ex) {
        logErrorAsJson("IndexOutOfBoundsException", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage() != null ? ex.getMessage() : "Index out of bounds"
        );
        problemDetail.setTitle("Index Out of Bounds");
        
        return problemDetail;
    }

    /**
     * Handles ArithmeticException.
     * Returns HTTP 500 with error details.
     */
    @ExceptionHandler(ArithmeticException.class)
    public ProblemDetail handleArithmeticException(ArithmeticException ex) {
        logErrorAsJson("ArithmeticException", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getMessage() != null ? ex.getMessage() : "Arithmetic error occurred"
        );
        problemDetail.setTitle("Arithmetic Error");
        
        return problemDetail;
    }

    /**
     * Handles DateTimeParseException.
     * Returns HTTP 400 with error details.
     */
    @ExceptionHandler(DateTimeParseException.class)
    public ProblemDetail handleDateTimeParseException(DateTimeParseException ex) {
        logErrorAsJson("DateTimeParseException", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage() != null ? ex.getMessage() : "Invalid date format"
        );
        problemDetail.setTitle("Date Parse Error");
        
        return problemDetail;
    }

    /**
     * Handles all other exceptions (fallback handler).
     * Returns HTTP 500 with generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        logErrorAsJson("Exception", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        
        return problemDetail;
    }

    /**
     * Logs error as JSON to production.log file.
     * Format: {"timestamp":"ISO-8601","errorType":"ExceptionType","message":"error message","stackTrace":["line1","line2","line3"]}
     * 
     * @param errorType The type of exception
     * @param ex The exception object
     */
    private void logErrorAsJson(String errorType, Exception ex) {
        try {
            // Get timestamp in ISO format
            String timestamp = Instant.now().toString();
            
            // Get exception message
            String message = ex.getMessage() != null ? ex.getMessage() : "No message available";
            
            // Get first 3 lines of stack trace
            StackTraceElement[] stackTrace = ex.getStackTrace();
            StringBuilder stackTraceJson = new StringBuilder("[");
            int limit = Math.min(3, stackTrace.length);
            for (int i = 0; i < limit; i++) {
                if (i > 0) {
                    stackTraceJson.append(",");
                }
                stackTraceJson.append("\"")
                    .append(escapeJson(stackTrace[i].toString()))
                    .append("\"");
            }
            stackTraceJson.append("]");
            
            // Build JSON string
            String jsonLog = String.format(
                "{\"timestamp\":\"%s\",\"errorType\":\"%s\",\"message\":\"%s\",\"stackTrace\":%s}%n",
                timestamp,
                errorType,
                escapeJson(message),
                stackTraceJson.toString()
            );
            
            // Write to production.log file
            Files.write(
                Paths.get(LOG_FILE),
                jsonLog.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            
            // Also log to console for debugging
            log.error("Exception logged to production.log: {}", errorType);
            
        } catch (IOException e) {
            log.error("Failed to write to production.log", e);
        }
    }

    /**
     * Escapes special characters in JSON strings.
     * 
     * @param input The string to escape
     * @return The escaped string
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}

// Made with Bob
