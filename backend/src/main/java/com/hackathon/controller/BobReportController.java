package com.hackathon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hackathon.model.BobGenerationEvent;
import com.hackathon.service.BobCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for exposing Bob's activity report for judging purposes.
 * Provides endpoints to view, analyze, and export Bob's code generation activities.
 */
@RestController
@RequestMapping("/api/bob-report")
@CrossOrigin
@Slf4j
public class BobReportController {

    private final BobCodeGenerator bobCodeGenerator;
    private final ObjectMapper objectMapper;

    public BobReportController(BobCodeGenerator bobCodeGenerator) {
        this.bobCodeGenerator = bobCodeGenerator;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * GET /api/bob-report
     * Returns the complete list of all Bob generation events.
     *
     * @return ResponseEntity containing list of all generation events
     */
    @GetMapping
    public ResponseEntity<List<BobGenerationEvent>> getBobReport() {
        log.info("Fetching Bob's complete activity report");
        List<BobGenerationEvent> report = bobCodeGenerator.getBobReport();
        log.info("Retrieved {} generation events from Bob's report", report.size());
        return ResponseEntity.ok(report);
    }

    /**
     * GET /api/bob-report/summary
     * Returns a summary of Bob's activities with metadata.
     *
     * @return ResponseEntity containing summary statistics and metadata
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getReportSummary() {
        log.info("Fetching Bob's activity report summary");
        
        Map<String, Object> summary = bobCodeGenerator.getReportSummary();
        
        // Add additional metadata
        summary.put("reportGeneratedAt", Instant.now().toString());
        
        Map<String, String> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        summary.put("systemInfo", systemInfo);
        
        log.info("Generated summary with {} total fixes", summary.get("totalFixes"));
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /api/bob-report/stats
     * Returns detailed statistics calculated from Bob's report.
     *
     * @return ResponseEntity containing calculated statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getReportStats() {
        log.info("Calculating Bob's activity statistics");
        
        List<BobGenerationEvent> events = bobCodeGenerator.getBobReport();
        Map<String, Object> stats = new HashMap<>();
        
        // Total fixes generated
        int totalFixes = events.size();
        stats.put("totalFixes", totalFixes);
        
        // Fixes by error type (breakdown)
        Map<String, Long> fixesByType = events.stream()
            .collect(Collectors.groupingBy(
                BobGenerationEvent::errorType,
                Collectors.counting()
            ));
        stats.put("fixesByType", fixesByType);
        
        // Average fix generation time (placeholder - would need timing data)
        stats.put("averageFixGenerationTime", "N/A - timing data not available");
        
        // Success rate (assuming all generated fixes are successful)
        double successRate = totalFixes > 0 ? 100.0 : 0.0;
        stats.put("successRate", successRate + "%");
        
        // Most common error type
        String mostCommonErrorType = fixesByType.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        stats.put("mostCommonErrorType", mostCommonErrorType);
        
        // Additional statistics
        stats.put("uniqueErrorTypes", fixesByType.size());
        stats.put("firstFixTimestamp", events.isEmpty() ? null : events.get(0).timestamp());
        stats.put("lastFixTimestamp", events.isEmpty() ? null : events.get(events.size() - 1).timestamp());
        
        log.info("Calculated statistics: {} total fixes, {} unique error types", 
                 totalFixes, fixesByType.size());
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /api/bob-report/clear
     * Clears Bob's activity report.
     *
     * @return ResponseEntity containing success message
     */
    @PostMapping("/clear")
    public ResponseEntity<Map<String, String>> clearReport() {
        log.warn("Clearing Bob's activity report");
        
        int eventCount = bobCodeGenerator.getBobReport().size();
        bobCodeGenerator.clearReport();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Bob's report has been cleared successfully");
        response.put("eventsCleared", String.valueOf(eventCount));
        response.put("clearedAt", Instant.now().toString());
        
        log.info("Successfully cleared {} events from Bob's report", eventCount);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/bob-report/export
     * Exports Bob's complete report as a formatted JSON file.
     *
     * @return ResponseEntity containing formatted JSON string with download headers
     */
    @GetMapping("/export")
    public ResponseEntity<String> exportReport() {
        log.info("Exporting Bob's activity report");
        
        try {
            List<BobGenerationEvent> report = bobCodeGenerator.getBobReport();
            Map<String, Object> exportData = new HashMap<>();
            
            // Add metadata
            exportData.put("exportedAt", Instant.now().toString());
            exportData.put("totalEvents", report.size());
            exportData.put("bobVersion", "1.0.0");
            
            // Add the actual report
            exportData.put("events", report);
            
            // Add summary
            exportData.put("summary", bobCodeGenerator.getReportSummary());
            
            // Convert to formatted JSON
            String jsonContent = objectMapper.writeValueAsString(exportData);
            
            // Set headers for download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=bob-report-" + Instant.now().getEpochSecond() + ".json");
            
            log.info("Successfully exported Bob's report with {} events", report.size());
            return ResponseEntity.ok()
                .headers(headers)
                .body(jsonContent);
                
        } catch (Exception e) {
            log.error("Error exporting Bob's report", e);
            return ResponseEntity.internalServerError()
                .body("{\"error\": \"Failed to export report: " + e.getMessage() + "\"}");
        }
    }
}

// Made with Bob