package com.hackathon.model;

/**
 * Record representing the analysis result of a log entry.
 * Contains information about whether an error is fixable and suggested fixes.
 */
public record AnalysisResult(
    boolean fixable,
    String errorType,
    String suggestedFix,
    String originalCodeSnippet,
    String logLine
) {
    // Convenience methods for backward compatibility
    public boolean isFixable() {
        return fixable;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public String codeSnippet() {
        return originalCodeSnippet;
    }
    
    public String getCodeSnippet() {
        return originalCodeSnippet;
    }
    
    public String getMessage() {
        return suggestedFix != null ? suggestedFix : "No fix available";
    }
    
    public String getStackTrace() {
        return logLine;
    }
}

// Made with Bob
