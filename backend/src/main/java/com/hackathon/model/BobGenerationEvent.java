package com.hackathon.model;

/**
 * Record representing a code generation event by Bob.
 * Used for tracking and reporting Bob's activities during the hackathon.
 */
public record BobGenerationEvent(
    String timestamp,
    String errorType,
    String generatedSnippet,
    String originalCode
) {
}

// Made with Bob
