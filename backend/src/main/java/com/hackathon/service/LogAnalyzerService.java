package com.hackathon.service;

import com.hackathon.model.AnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for analyzing error logs from production.log.
 * Determines if errors are fixable by Bob and provides suggested fixes.
 */
@Service
@Slf4j
public class LogAnalyzerService {

    // Regex patterns for detecting error types
    private static final Pattern ERROR_TYPE_PATTERN = Pattern.compile("\"errorType\":\"(\\w+)\"");
    private static final Pattern STACK_TRACE_PATTERN = Pattern.compile("\"stackTrace\":\\[(.*?)\\]");
    private static final Pattern HACKATHON_PACKAGE_PATTERN = Pattern.compile("com\\.hackathon\\.[^\"]+");

    /**
     * Analyzes a single log line and determines if the error is fixable.
     * 
     * @param logLine The JSON log line to analyze
     * @return AnalysisResult containing fixability information and suggested fix
     */
    public AnalysisResult analyzeLogLine(String logLine) {
        if (logLine == null || logLine.trim().isEmpty()) {
            return null;
        }

        // Extract error type from JSON
        String errorType = extractErrorType(logLine);
        
        // Return null for non-error lines (lines without errorType in JSON)
        if ("Unknown".equals(errorType)) {
            return null;
        }
        
        // Extract code snippet from stack trace
        String codeSnippet = extractCodeSnippet(logLine);
        
        // Determine fixability and suggested fix based on error type
        boolean fixable;
        String suggestedFix;
        
        switch (errorType) {
            case "NullPointerException":
                fixable = true;
                suggestedFix = "Add null check before accessing object";
                break;
                
            case "IndexOutOfBoundsException":
                fixable = true;
                suggestedFix = "Add bounds validation before accessing list";
                break;
                
            case "ArithmeticException":
                fixable = true;
                suggestedFix = "Add zero division check";
                break;
                
            case "DateTimeParseException":
                fixable = true;
                suggestedFix = "Add try-catch with fallback date parsing";
                break;
                
            default:
                fixable = false;
                suggestedFix = "Manual review required";
                break;
        }
        
        return new AnalysisResult(fixable, errorType, suggestedFix, codeSnippet, logLine);
    }

    /**
     * Analyzes all log entries in the specified log file.
     * 
     * @param filePath Path to the production.log file
     * @return List of AnalysisResult for each log entry
     */
    public List<AnalysisResult> analyzeLogFile(String filePath) {
        List<AnalysisResult> results = new ArrayList<>();
        
        try {
            // Read all lines from the log file
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            
            // Analyze each line
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    AnalysisResult result = analyzeLogLine(line);
                    results.add(result);
                }
            }
            
            log.info("Analyzed {} log entries from {}", results.size(), filePath);
            
        } catch (IOException e) {
            log.error("Failed to read log file: {}", filePath, e);
            // Return empty list on file not found or read error
        }
        
        return results;
    }

    /**
     * Extracts the error type from a JSON log line.
     * 
     * @param logLine The JSON log line
     * @return The error type, or "Unknown" if not found
     */
    private String extractErrorType(String logLine) {
        Matcher matcher = ERROR_TYPE_PATTERN.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown";
    }

    /**
     * Extracts the code snippet from the stack trace in a JSON log line.
     * Looks for the first stack trace element containing "com.hackathon" package.
     * 
     * @param logLine The JSON log line
     * @return The code snippet, or "Code location not identified" if not found
     */
    private String extractCodeSnippet(String logLine) {
        Matcher stackTraceMatcher = STACK_TRACE_PATTERN.matcher(logLine);
        
        if (stackTraceMatcher.find()) {
            String stackTraceContent = stackTraceMatcher.group(1);
            
            // Find the first occurrence of com.hackathon package in stack trace
            Matcher hackathonMatcher = HACKATHON_PACKAGE_PATTERN.matcher(stackTraceContent);
            if (hackathonMatcher.find()) {
                return hackathonMatcher.group();
            }
        }
        
        return "Code location not identified";
    }
}

// Made with Bob
