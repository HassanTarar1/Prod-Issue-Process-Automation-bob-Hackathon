package com.hackathon.controller;

import com.hackathon.model.AnalysisResult;
import com.hackathon.model.TestResult;
import com.hackathon.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main orchestrator controller for the auto-fix workflow.
 * Coordinates log analysis, code generation, GitHub PR creation, Slack notifications, and testing.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class AutoFixController {

    private final LogAnalyzerService logAnalyzerService;
    private final BobCodeGenerator bobCodeGenerator;
    private final GitHubService gitHubService;
    private final SlackNotifier slackNotifier;
    private final PlaywrightTestRunner playwrightTestRunner;
    private final PlaywrightCodegenService playwrightCodegenService;

    @Value("${github.token:}")
    private String githubToken;

    @Value("${slack.webhook.url:}")
    private String slackWebhookUrl;
    
    @Value("${github.repo.owner:demo}")
    private String githubRepoOwner;
    
    @Value("${github.repo.name:flight-dashboard}")
    private String githubRepoName;

    public AutoFixController(
            LogAnalyzerService logAnalyzerService,
            BobCodeGenerator bobCodeGenerator,
            GitHubService gitHubService,
            SlackNotifier slackNotifier,
            PlaywrightTestRunner playwrightTestRunner,
            PlaywrightCodegenService playwrightCodegenService) {
        this.logAnalyzerService = logAnalyzerService;
        this.bobCodeGenerator = bobCodeGenerator;
        this.gitHubService = gitHubService;
        this.slackNotifier = slackNotifier;
        this.playwrightTestRunner = playwrightTestRunner;
        this.playwrightCodegenService = playwrightCodegenService;
    }

    /**
     * Main auto-fix endpoint that orchestrates the entire workflow.
     * 
     * @param request containing logLine, repo (optional), and filePath (optional)
     * @return comprehensive result with fix details, PR URL, and test results
     */
    @PostMapping("/auto-fix")
    public Map<String, Object> autoFix(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String logLine = request.get("logLine");
            String repo = request.getOrDefault("repo", githubRepoOwner + "/" + githubRepoName);
            String filePath = request.getOrDefault("filePath", "src/main/java/com/hackathon/service/FlightService.java");

            log.info("Starting auto-fix workflow for log line");

            // Step 1: Analyze log line
            log.info("Step 1: Analyzing log line");
            AnalysisResult analysis = logAnalyzerService.analyzeLogLine(logLine);
            
            response.put("fixable", analysis.fixable());
            response.put("errorType", analysis.errorType());

            // If not fixable, return early
            if (!analysis.fixable()) {
                response.put("fixGenerated", false);
                response.put("fixSnippet", "");
                response.put("prUrl", "");
                response.put("slackNotified", false);
                response.put("message", "Error is not auto-fixable: " + analysis.suggestedFix());
                log.warn("Error type {} is not fixable", analysis.errorType());
                return response;
            }

            // Step 2: Generate fix using Bob
            log.info("Step 2: Generating fix for error type: {}", analysis.errorType());
            String fixSnippet = bobCodeGenerator.generateFix(analysis.errorType(), analysis.codeSnippet());
            
            response.put("fixGenerated", true);
            response.put("fixSnippet", fixSnippet);

            // Step 3: Create GitHub PR
            log.info("Step 3: Creating GitHub PR for repo: {}, file: {}", repo, filePath);
            String originalCode = analysis.codeSnippet();
            String fixedCode = originalCode + "\n" + fixSnippet;
            String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, analysis.errorType());
            
            response.put("prUrl", prUrl);

            // Step 4: Send Slack notification
            log.info("Step 4: Sending Slack notification");
            boolean slackNotified = slackNotifier.sendNotification(analysis.errorType(), fixSnippet, prUrl);
            response.put("slackNotified", slackNotified);

            // Step 5: Optionally run Playwright test (if test file exists)
            log.info("Step 5: Checking for Playwright tests");
            try {
                List<TestResult> testResults = playwrightTestRunner.runAllTests();
                if (!testResults.isEmpty()) {
                    boolean allTestsPassed = testResults.stream().allMatch(TestResult::passed);
                    response.put("testPassed", allTestsPassed);
                    log.info("Playwright tests executed. All passed: {}", allTestsPassed);
                }
            } catch (Exception e) {
                log.warn("Could not run Playwright tests: {}", e.getMessage());
            }

            response.put("message", "Auto-fix workflow completed successfully");
            log.info("Auto-fix workflow completed successfully for error type: {}", analysis.errorType());

        } catch (Exception e) {
            log.error("Error in auto-fix workflow", e);
            response.put("fixable", false);
            response.put("errorType", "Unknown");
            response.put("fixGenerated", false);
            response.put("fixSnippet", "");
            response.put("prUrl", "");
            response.put("slackNotified", false);
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }

    /**
     * Endpoint to record a Playwright test using codegen.
     * 
     * @param request containing url (optional, defaults to http://localhost:4200)
     * @return result with test file path
     */
    @PostMapping("/record-test")
    public Map<String, Object> recordTest(@RequestBody(required = false) Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String url = "http://localhost:4200";
            if (request != null && request.containsKey("url")) {
                url = request.get("url");
            }

            log.info("Starting Playwright test recording for URL: {}", url);

            // Check if Playwright is installed
            boolean playwrightInstalled = playwrightCodegenService.isPlaywrightInstalled();
            
            if (!playwrightInstalled) {
                response.put("success", false);
                response.put("testFilePath", "");
                response.put("message", "Error: Playwright is not installed. Please run 'npm install -D @playwright/test' and 'npx playwright install'");
                log.error("Playwright is not installed");
                return response;
            }

            // Generate test
            String testFilePath = playwrightCodegenService.generateTest(url);
            
            if (testFilePath.startsWith("Error:")) {
                response.put("success", false);
                response.put("testFilePath", "");
                response.put("message", testFilePath);
                log.error("Failed to generate test: {}", testFilePath);
            } else {
                response.put("success", true);
                response.put("testFilePath", testFilePath);
                response.put("message", "Test recorded successfully");
                log.info("Test recorded successfully: {}", testFilePath);
            }

        } catch (Exception e) {
            log.error("Error recording test", e);
            response.put("success", false);
            response.put("testFilePath", "");
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }

    /**
     * Endpoint to get system status and configuration.
     * 
     * @return system status information
     */
    @GetMapping("/auto-fix/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Checking system status");

            // Check Playwright installation
            boolean playwrightInstalled = playwrightCodegenService.isPlaywrightInstalled();
            response.put("playwrightInstalled", playwrightInstalled);

            // Check GitHub configuration
            boolean githubConfigured = githubToken != null && !githubToken.trim().isEmpty();
            response.put("githubConfigured", githubConfigured);

            // Check Slack configuration
            boolean slackConfigured = slackWebhookUrl != null && !slackWebhookUrl.trim().isEmpty();
            response.put("slackConfigured", slackConfigured);

            // Get Bob report summary
            Map<String, Object> bobSummary = bobCodeGenerator.getReportSummary();
            int totalFixesGenerated = (int) bobSummary.getOrDefault("totalFixes", 0);
            response.put("totalFixesGenerated", totalFixesGenerated);

            // Build status message
            StringBuilder message = new StringBuilder("System Status: ");
            List<String> issues = new ArrayList<>();
            
            if (!playwrightInstalled) {
                issues.add("Playwright not installed");
            }
            if (!githubConfigured) {
                issues.add("GitHub token not configured");
            }
            if (!slackConfigured) {
                issues.add("Slack webhook not configured");
            }

            if (issues.isEmpty()) {
                message.append("All systems operational");
            } else {
                message.append("Issues detected - ").append(String.join(", ", issues));
            }

            response.put("message", message.toString());
            log.info("System status check completed: {}", message);

        } catch (Exception e) {
            log.error("Error checking system status", e);
            response.put("playwrightInstalled", false);
            response.put("githubConfigured", false);
            response.put("slackConfigured", false);
            response.put("totalFixesGenerated", 0);
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }

    /**
     * Endpoint to process multiple errors from a log file in batch.
     * 
     * @param request containing logFilePath
     * @return batch processing summary
     */
    @PostMapping("/auto-fix/batch")
    public Map<String, Object> batchAutoFix(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String logFilePath = request.get("logFilePath");
            
            if (logFilePath == null || logFilePath.trim().isEmpty()) {
                response.put("totalErrors", 0);
                response.put("fixableErrors", 0);
                response.put("fixesGenerated", 0);
                response.put("prsCreated", new ArrayList<>());
                response.put("message", "Error: logFilePath is required");
                return response;
            }

            log.info("Starting batch auto-fix for log file: {}", logFilePath);

            // Step 1: Analyze entire log file
            log.info("Step 1: Analyzing log file");
            List<AnalysisResult> analysisResults = logAnalyzerService.analyzeLogFile(logFilePath);
            
            int totalErrors = analysisResults.size();
            List<AnalysisResult> fixableResults = analysisResults.stream()
                    .filter(AnalysisResult::fixable)
                    .collect(Collectors.toList());
            int fixableErrors = fixableResults.size();

            response.put("totalErrors", totalErrors);
            response.put("fixableErrors", fixableErrors);

            // Step 2: Generate fixes and create PRs for each fixable error
            log.info("Step 2: Processing {} fixable errors", fixableErrors);
            List<String> prUrls = new ArrayList<>();
            int fixesGenerated = 0;

            for (AnalysisResult result : fixableResults) {
                try {
                    // Generate fix
                    String fixSnippet = bobCodeGenerator.generateFix(result.errorType(), result.codeSnippet());
                    fixesGenerated++;

                    // Create PR (using environment variables for repo and default file path)
                    String repo = githubRepoOwner + "/" + githubRepoName;
                    String filePath = "src/main/java/com/hackathon/service/FlightService.java";
                    String originalCode = result.codeSnippet();
                    String fixedCode = originalCode + "\n" + fixSnippet;
                    String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, result.errorType());
                    
                    prUrls.add(prUrl);
                    log.info("Created PR for {}: {}", result.errorType(), prUrl);

                } catch (Exception e) {
                    log.error("Error processing fixable error: {}", result.errorType(), e);
                }
            }

            response.put("fixesGenerated", fixesGenerated);
            response.put("prsCreated", prUrls);

            // Step 3: Send batch notification to Slack
            log.info("Step 3: Sending batch notification to Slack");
            if (fixesGenerated > 0) {
                String batchSummary = String.format("Batch Auto-Fix Summary:\n" +
                        "Total Errors: %d\n" +
                        "Fixable Errors: %d\n" +
                        "Fixes Generated: %d\n" +
                        "PRs Created: %d",
                        totalErrors, fixableErrors, fixesGenerated, prUrls.size());
                
                slackNotifier.sendNotification("Batch Processing", batchSummary, 
                        prUrls.isEmpty() ? "No PRs" : prUrls.get(0));
            }

            response.put("message", String.format("Batch processing completed: %d/%d errors fixed", 
                    fixesGenerated, totalErrors));
            log.info("Batch auto-fix completed: {} fixes generated from {} total errors", 
                    fixesGenerated, totalErrors);

        } catch (Exception e) {
            log.error("Error in batch auto-fix", e);
            response.put("totalErrors", 0);
            response.put("fixableErrors", 0);
            response.put("fixesGenerated", 0);
            response.put("prsCreated", new ArrayList<>());
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }
}

// Made with Bob