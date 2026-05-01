package com.hackathon.service;

import com.hackathon.model.BobGenerationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Service responsible for generating code fixes for detected errors.
 * Maintains a report of all Bob's activities for judging purposes.
 */
@Service
@Slf4j
public class BobCodeGenerator {

    private final List<BobGenerationEvent> generationEvents = new CopyOnWriteArrayList<>();

    /**
     * Generates a fix snippet based on the error type.
     *
     * @param errorType    the type of error to fix
     * @param originalCode the original problematic code (optional)
     * @return the generated fix snippet
     */
    public String generateFix(String errorType, String originalCode) {
        String fixSnippet;

        switch (errorType) {
            case "NullPointerException" -> fixSnippet = """
                if (obj == null) {
                    log.warn("Null object detected, returning default value");
                    return;
                }
                """;

            case "IndexOutOfBoundsException" -> fixSnippet = """
                if (index < 0 || index >= list.size()) {
                    log.warn("Index out of bounds: {}, list size: {}", index, list.size());
                    return defaultValue;
                }
                """;

            case "ArithmeticException" -> fixSnippet = """
                if (denominator == 0) {
                    log.warn("Division by zero detected, returning 0");
                    return 0;
                }
                """;

            case "DateTimeParseException" -> fixSnippet = """
                try {
                    return parseDefault(date);
                } catch (DateTimeParseException e) {
                    log.warn("Date parse error, using fallback date");
                    return LocalDateTime.now();
                }
                """;

            default -> {
                log.warn("Unknown error type: {}", errorType);
                fixSnippet = "// No fix available for error type: " + errorType;
            }
        }

        // Record the generation event
        String timestamp = Instant.now().toString();
        BobGenerationEvent event = new BobGenerationEvent(
            timestamp,
            errorType,
            fixSnippet,
            originalCode
        );
        generationEvents.add(event);

        log.info("Generated fix for {}: {} characters", errorType, fixSnippet.length());

        return fixSnippet;
    }

    /**
     * Returns all recorded generation events for reporting.
     *
     * @return list of all Bob generation events
     */
    public List<BobGenerationEvent> getBobReport() {
        return List.copyOf(generationEvents);
    }

    /**
     * Clears all recorded generation events.
     * Used for testing and reset purposes.
     */
    public void clearReport() {
        generationEvents.clear();
        log.info("Bob report cleared");
    }

    /**
     * Returns a summary of Bob's activities.
     *
     * @return map containing summary statistics
     */
    public Map<String, Object> getReportSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Total fixes
        summary.put("totalFixes", generationEvents.size());

        // Fixes by type
        Map<String, Long> fixesByType = generationEvents.stream()
            .collect(Collectors.groupingBy(
                BobGenerationEvent::errorType,
                Collectors.counting()
            ));
        summary.put("fixesByType", fixesByType);

        // Last fix timestamp
        String lastFixTimestamp = generationEvents.isEmpty()
            ? null
            : generationEvents.get(generationEvents.size() - 1).timestamp();
        summary.put("lastFixTimestamp", lastFixTimestamp);

        // Bob version
        summary.put("bobVersion", "1.0.0");

        return summary;
    }
}

// Made with Bob
