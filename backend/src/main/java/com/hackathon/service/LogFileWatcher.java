package com.hackathon.service;

import com.hackathon.model.AnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that automatically monitors the production.log file for new errors
 * and triggers the auto-fix workflow.
 */
@Service
@Slf4j
public class LogFileWatcher {

    private final LogAnalyzerService logAnalyzerService;
    private final BobCodeGenerator bobCodeGenerator;
    private final GitHubService gitHubService;
    private final SlackNotifier slackNotifier;

    private int lastProcessedLine = 0;
    private boolean isEnabled = false;
    private String logFilePath = "production.log";

    public LogFileWatcher(LogAnalyzerService logAnalyzerService,
                         BobCodeGenerator bobCodeGenerator,
                         GitHubService gitHubService,
                         SlackNotifier slackNotifier) {
        this.logAnalyzerService = logAnalyzerService;
        this.bobCodeGenerator = bobCodeGenerator;
        this.gitHubService = gitHubService;
        this.slackNotifier = slackNotifier;
    }

    @PostConstruct
    public void initialize() {
        log.info("LogFileWatcher initialized. Watcher is disabled by default. Use enable() to start monitoring.");
    }

    /**
     * Scheduled method that watches the log file for new errors every 5 seconds.
     */
    @Scheduled(fixedDelay = 5000)
    public void watchLogFile() {
        // Check if watcher is enabled
        if (!isEnabled) {
            return;
        }

        try {
            // Check if production.log exists
            Path logPath = Paths.get(logFilePath);
            if (!Files.exists(logPath)) {
                log.debug("Log file does not exist: {}", logFilePath);
                return;
            }

            // Read all lines from the log file
            List<String> allLines = Files.readAllLines(logPath);
            
            // Process only new lines
            int totalLines = allLines.size();
            if (lastProcessedLine >= totalLines) {
                // No new lines to process
                return;
            }

            int newErrorsCount = 0;
            
            // Process each new line
            for (int i = lastProcessedLine; i < totalLines; i++) {
                String logLine = allLines.get(i);
                
                if (logLine != null && !logLine.trim().isEmpty()) {
                    // Analyze the log line
                    AnalysisResult result = logAnalyzerService.analyzeLogLine(logLine);
                    
                    // If fixable, trigger auto-fix workflow asynchronously
                    if (result.isFixable()) {
                        newErrorsCount++;
                        processError(logLine);
                    }
                }
            }
            
            // Update last processed line
            lastProcessedLine = totalLines;
            
            if (newErrorsCount > 0) {
                log.info("Processed {} new fixable error(s) from log file", newErrorsCount);
            }
            
        } catch (IOException e) {
            log.error("Error reading log file: {}", logFilePath, e);
        } catch (Exception e) {
            log.error("Unexpected error in watchLogFile: {}", e.getMessage(), e);
        }
    }

    /**
     * Processes a single error asynchronously by triggering the auto-fix workflow.
     *
     * @param logLine the log line containing the error
     */
    @Async
    private void processError(String logLine) {
        try {
            log.info("Processing error asynchronously: {}", logLine.substring(0, Math.min(100, logLine.length())));
            
            // Analyze log line
            AnalysisResult analysis = logAnalyzerService.analyzeLogLine(logLine);
            
            if (!analysis.isFixable()) {
                log.debug("Error is not fixable, skipping");
                return;
            }
            
            // Generate fix
            String fixedCode = bobCodeGenerator.generateFix(analysis.getErrorType(), analysis.getCodeSnippet());
            
            // Create GitHub PR (using default repo/file path)
            String defaultRepo = "ibm-bob/hackathon-demo";
            String defaultFilePath = "src/main/java/com/hackathon/service/FlightService.java";
            String prUrl = gitHubService.createPullRequest(
                defaultRepo,
                defaultFilePath,
                analysis.getCodeSnippet(),
                fixedCode,
                analysis.getErrorType()
            );
            
            // Send Slack notification
            slackNotifier.sendNotification(analysis.getErrorType(), fixedCode, prUrl);
            
            log.info("Successfully processed error: {} - PR: {}", analysis.getErrorType(), prUrl);
            
        } catch (Exception e) {
            log.error("Error processing error asynchronously: {}", e.getMessage(), e);
        }
    }

    /**
     * Enables the log file watcher.
     */
    public void enable() {
        isEnabled = true;
        log.info("LogFileWatcher enabled. Monitoring log file: {}", logFilePath);
    }

    /**
     * Disables the log file watcher.
     */
    public void disable() {
        isEnabled = false;
        log.info("LogFileWatcher disabled. Stopped monitoring log file: {}", logFilePath);
    }

    /**
     * Resets the last processed line counter to start from the beginning.
     */
    public void reset() {
        lastProcessedLine = 0;
        log.info("LogFileWatcher reset. Last processed line set to 0");
    }

    /**
     * Gets the current status of the log file watcher.
     *
     * @return map containing status information
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", isEnabled);
        status.put("lastProcessedLine", lastProcessedLine);
        status.put("logFilePath", logFilePath);
        
        // Check if log file exists
        Path logPath = Paths.get(logFilePath);
        status.put("logFileExists", Files.exists(logPath));
        
        return status;
    }
}

// Made with Bob