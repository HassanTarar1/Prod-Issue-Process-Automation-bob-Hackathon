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
}

// Made with Bob
