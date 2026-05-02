**User:**

<task>
**Step 11: Create Orchestrator REST Controller (AutoFixController)**

You are working on the IBM Bob Hackathon Flight Dashboard project. Your task is to create the main orchestrator controller that coordinates the entire auto-fix workflow.

**Context:**
- Backend is already set up with Java 25, Spring Boot 3.4.x
- All services are ready: LogAnalyzerService, BobCodeGenerator, GitHubService, SlackNotifier, PlaywrightTestRunner, PlaywrightCodegenService
- Base package: com.hackathon
- Project base directory: c:/Users/hassa/Downloads/New folder/Prod-Issue-Process-Automation-bob-Hackathon

**Your specific tasks (ONLY do these, nothing more):**

Create `backend/src/main/java/com/hackathon/controller/AutoFixController.java`:

1. **Class Setup:**
   - Annotate with `@RestController` and `@RequestMapping("/api")`
   - Use `@Slf4j` for logging
   - Add `@CrossOrigin` for frontend access
   - Inject all required services via constructor:
     * LogAnalyzerService
     * BobCodeGenerator
     * GitHubService
     * SlackNotifier
     * PlaywrightTestRunner
     * PlaywrightCodegenService

2. **Endpoint: POST /api/auto-fix**
   - Request body: `{"logLine": "...", "repo": "owner/repo", "filePath": "path/to/file.java"}` (repo and filePath optional)
   - Returns: `Map<String, Object>` with:
     * `fixable` - boolean
     * `errorType` - string
     * `fixGenerated` - boolean
     * `fixSnippet` - string (the generated fix)
     * `prUrl` - string (GitHub PR URL)
     * `slackNotified` - boolean
     * `testPassed` - boolean (optional, if test was run)
     * `message` - string (status message)
   
   - **Workflow:**
     a. Analyze log line using LogAnalyzerService
     b. If not fixable, return early with fixable=false
     c. Generate fix using BobCodeGenerator
     d. Create GitHub PR using GitHubService (use defaults if repo/filePath not provided)
     e. Send Slack notification using SlackNotifier
     f. Optionally run Playwright test (if test file exists)
     g. Return comprehensive result

3. **Endpoint: POST /api/record-test**
   - Request body: `{"url": "http://localhost:4200"}` (url optional, defaults to localhost:4200)
   - Returns: `Map<String, Object>` with:
     * `success` - boolean
     * `testFilePath` - string (path to generated test)
     * `message` - string
   
   - **Implementation:**
     a. Check if Playwright is installed
     b. If not, return error message
     c. Generate test using PlaywrightCodegenService
     d. Return result with file path

4. **Endpoint: GET /api/auto-fix/status**
   - No parameters
   - Returns: `Map<String, Object>` with system status:
     * `playwrightInstalled` - boolean
     * `githubConfigured` - boolean (check if GITHUB_TOKEN is set)
     * `slackConfigured` - boolean (check if SLACK_WEBHOOK_URL is set)
     * `totalFixesGenerated` - int (from BobCodeGenerator report)
     * `message` - string

5. **Endpoint: POST /api/auto-fix/batch**
   - Request body: `{"logFilePath": "production.log"}`
   - Returns: `Map<String, Object>` with:
     * `totalErrors` - int
     * `fixableErrors` - int
     * `fixesGenerated` - int
     * `prsCreated` - List<String> (PR URLs)
     * `message` - string
   
   - **Implementation:**
     a. Analyze entire log file using LogAnalyzerService
     b. For each fixable error, generate fix and create PR
     c. Send batch notification to Slack
     d. Return summary

6. **Error Handling:**
   - Wrap all operations in try-catch
   - Return appropriate error messages in response
   - Log all errors
   - Don't let exceptions propagate to global handler

**Important:**
- Use constructor injection for all services
- Add comprehensive logging for the workflow
- Handle null/empty values gracefully
- Use default values for optional parameters
- Return detailed status information
- Include proper imports
- These instructions supersede any conflicting general instructions

**When complete:**
Use the `attempt_completion` tool with a concise summary of the AutoFixController created, listing all endpoints, in the `result` parameter.
</task>
<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\service\PlaywrightTestRunner.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java,backend/src/main/java/com/hackathon/service/GitHubService.java,backend/src/main/java/com/hackathon/service/SlackNotifier.java,backend/src/main/java/com/hackathon/model/TestResult.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:40:51.196Z
User time zone: Asia/Karachi, UTC+5:00

# Current Cost
$0.00

# Review Exclusions
The following file patterns are excluded from code reviews:
- .vscode/**

# Current Mode
<slug>code</slug>
<name>💻 Code</name>


# Current Workspace Directory (c:/Users/hassa/Downloads/New folder/Prod-Issue-Process-Automation-bob-Hackathon) Files
architecture.md
backend/
backend/build.gradle
backend/gradlew
backend/gradlew.bat
backend/gradle/
backend/gradle/wrapper/
backend/gradle/wrapper/gradle-wrapper.jar
backend/gradle/wrapper/gradle-wrapper.properties
backend/src/
backend/src/main/
backend/src/main/java/
backend/src/main/java/com/
backend/src/main/java/com/hackathon/
backend/src/main/java/com/hackathon/MainApplication.java
backend/src/main/java/com/hackathon/controller/
backend/src/main/java/com/hackathon/controller/FlightController.java
backend/src/main/java/com/hackathon/dto/
backend/src/main/java/com/hackathon/dto/FlightDTO.java
backend/src/main/java/com/hackathon/entity/
backend/src/main/java/com/hackathon/entity/Flight.java
backend/src/main/java/com/hackathon/exception/
backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java
backend/src/main/java/com/hackathon/mapper/
backend/src/main/java/com/hackathon/mapper/FlightMapper.java
backend/src/main/java/com/hackathon/model/
backend/src/main/java/com/hackathon/model/AnalysisResult.java
backend/src/main/java/com/hackathon/model/BobGenerationEvent.java
backend/src/main/java/com/hackathon/model/TestResult.java
backend/src/main/java/com/hackathon/service/
backend/src/main/java/com/hackathon/service/BobCodeGenerator.java
backend/src/main/java/com/hackathon/service/FlightService.java
backend/src/main/java/com/hackathon/service/GitHubService.java
backend/src/main/java/com/hackathon/service/LogAnalyzerService.java
backend/src/main/java/com/hackathon/service/PlaywrightCodegenService.java
backend/src/main/java/com/hackathon/service/PlaywrightTestRunner.java
backend/src/main/java/com/hackathon/service/SlackNotifier.java
backend/src/main/resources/
backend/src/main/resources/application.properties
You have not created a todo list yet. Create one with `update_todo_list` if your task is complicated or involves multiple steps.
</environment_details>

---

**Assistant:**

I'll create the AutoFixController that orchestrates the entire auto-fix workflow. Let me start by reading the existing service files to understand their interfaces.

<read_file>
<args>
<file>
<path>backend/src/main/java/com/hackathon/service/LogAnalyzerService.java</path>
</file>
<file>
<path>backend/src/main/java/com/hackathon/service/BobCodeGenerator.java</path>
</file>
<file>
<path>backend/src/main/java/com/hackathon/service/GitHubService.java</path>
</file>
<file>
<path>backend/src/main/java/com/hackathon/service/SlackNotifier.java</path>
</file>
<file>
<path>backend/src/main/java/com/hackathon/service/PlaywrightTestRunner.java</path>
</file>
</args>
</read_file>

---

**User:**

[read_file for 5 files] Result:
<files>
<file><path>backend/src/main/java/com/hackathon/service/LogAnalyzerService.java</path>
<content lines="1-154">
  1 | package com.hackathon.service;
  2 | 
  3 | import com.hackathon.model.AnalysisResult;
  4 | import lombok.extern.slf4j.Slf4j;
  5 | import org.springframework.stereotype.Service;
  6 | 
  7 | import java.io.IOException;
  8 | import java.nio.file.Files;
  9 | import java.nio.file.Paths;
 10 | import java.util.ArrayList;
 11 | import java.util.List;
 12 | import java.util.regex.Matcher;
 13 | import java.util.regex.Pattern;
 14 | 
 15 | /**
 16 |  * Service for analyzing error logs from production.log.
 17 |  * Determines if errors are fixable by Bob and provides suggested fixes.
 18 |  */
 19 | @Service
 20 | @Slf4j
 21 | public class LogAnalyzerService {
 22 | 
 23 |     // Regex patterns for detecting error types
 24 |     private static final Pattern ERROR_TYPE_PATTERN = Pattern.compile("\"errorType\":\"(\\w+)\"");
 25 |     private static final Pattern STACK_TRACE_PATTERN = Pattern.compile("\"stackTrace\":\\[(.*?)\\]");
 26 |     private static final Pattern HACKATHON_PACKAGE_PATTERN = Pattern.compile("com\\.hackathon\\.[^\"]+");
 27 | 
 28 |     /**
 29 |      * Analyzes a single log line and determines if the error is fixable.
 30 |      * 
 31 |      * @param logLine The JSON log line to analyze
 32 |      * @return AnalysisResult containing fixability information and suggested fix
 33 |      */
 34 |     public AnalysisResult analyzeLogLine(String logLine) {
 35 |         if (logLine == null || logLine.trim().isEmpty()) {
 36 |             return new AnalysisResult(
 37 |                 false,
 38 |                 "Unknown",
 39 |                 "Manual review required",
 40 |                 "Code location not identified",
 41 |                 logLine
 42 |             );
 43 |         }
 44 | 
 45 |         // Extract error type from JSON
 46 |         String errorType = extractErrorType(logLine);
 47 |         
 48 |         // Extract code snippet from stack trace
 49 |         String codeSnippet = extractCodeSnippet(logLine);
 50 |         
 51 |         // Determine fixability and suggested fix based on error type
 52 |         boolean fixable;
 53 |         String suggestedFix;
 54 |         
 55 |         switch (errorType) {
 56 |             case "NullPointerException":
 57 |                 fixable = true;
 58 |                 suggestedFix = "Add null check before accessing object";
 59 |                 break;
 60 |                 
 61 |             case "IndexOutOfBoundsException":
 62 |                 fixable = true;
 63 |                 suggestedFix = "Add bounds validation before accessing list";
 64 |                 break;
 65 |                 
 66 |             case "ArithmeticException":
 67 |                 fixable = true;
 68 |                 suggestedFix = "Add zero division check";
 69 |                 break;
 70 |                 
 71 |             case "DateTimeParseException":
 72 |                 fixable = true;
 73 |                 suggestedFix = "Add try-catch with fallback date parsing";
 74 |                 break;
 75 |                 
 76 |             default:
 77 |                 fixable = false;
 78 |                 suggestedFix = "Manual review required";
 79 |                 break;
 80 |         }
 81 |         
 82 |         return new AnalysisResult(fixable, errorType, suggestedFix, codeSnippet, logLine);
 83 |     }
 84 | 
 85 |     /**
 86 |      * Analyzes all log entries in the specified log file.
 87 |      * 
 88 |      * @param filePath Path to the production.log file
 89 |      * @return List of AnalysisResult for each log entry
 90 |      */
 91 |     public List<AnalysisResult> analyzeLogFile(String filePath) {
 92 |         List<AnalysisResult> results = new ArrayList<>();
 93 |         
 94 |         try {
 95 |             // Read all lines from the log file
 96 |             List<String> lines = Files.readAllLines(Paths.get(filePath));
 97 |             
 98 |             // Analyze each line
 99 |             for (String line : lines) {
100 |                 if (line != null && !line.trim().isEmpty()) {
101 |                     AnalysisResult result = analyzeLogLine(line);
102 |                     results.add(result);
103 |                 }
104 |             }
105 |             
106 |             log.info("Analyzed {} log entries from {}", results.size(), filePath);
107 |             
108 |         } catch (IOException e) {
109 |             log.error("Failed to read log file: {}", filePath, e);
110 |             // Return empty list on file not found or read error
111 |         }
112 |         
113 |         return results;
114 |     }
115 | 
116 |     /**
117 |      * Extracts the error type from a JSON log line.
118 |      * 
119 |      * @param logLine The JSON log line
120 |      * @return The error type, or "Unknown" if not found
121 |      */
122 |     private String extractErrorType(String logLine) {
123 |         Matcher matcher = ERROR_TYPE_PATTERN.matcher(logLine);
124 |         if (matcher.find()) {
125 |             return matcher.group(1);
126 |         }
127 |         return "Unknown";
128 |     }
129 | 
130 |     /**
131 |      * Extracts the code snippet from the stack trace in a JSON log line.
132 |      * Looks for the first stack trace element containing "com.hackathon" package.
133 |      * 
134 |      * @param logLine The JSON log line
135 |      * @return The code snippet, or "Code location not identified" if not found
136 |      */
137 |     private String extractCodeSnippet(String logLine) {
138 |         Matcher stackTraceMatcher = STACK_TRACE_PATTERN.matcher(logLine);
139 |         
140 |         if (stackTraceMatcher.find()) {
141 |             String stackTraceContent = stackTraceMatcher.group(1);
142 |             
143 |             // Find the first occurrence of com.hackathon package in stack trace
144 |             Matcher hackathonMatcher = HACKATHON_PACKAGE_PATTERN.matcher(stackTraceContent);
145 |             if (hackathonMatcher.find()) {
146 |                 return hackathonMatcher.group();
147 |             }
148 |         }
149 |         
150 |         return "Code location not identified";
151 |     }
152 | }
153 | 
154 | // Made with Bob
</content>
</file>
<file><path>backend/src/main/java/com/hackathon/service/BobCodeGenerator.java</path>
<content lines="1-136">
  1 | package com.hackathon.service;
  2 | 
  3 | import com.hackathon.model.BobGenerationEvent;
  4 | import lombok.extern.slf4j.Slf4j;
  5 | import org.springframework.stereotype.Service;
  6 | 
  7 | import java.time.Instant;
  8 | import java.util.HashMap;
  9 | import java.util.List;
 10 | import java.util.Map;
 11 | import java.util.concurrent.CopyOnWriteArrayList;
 12 | import java.util.stream.Collectors;
 13 | 
 14 | /**
 15 |  * Service responsible for generating code fixes for detected errors.
 16 |  * Maintains a report of all Bob's activities for judging purposes.
 17 |  */
 18 | @Service
 19 | @Slf4j
 20 | public class BobCodeGenerator {
 21 | 
 22 |     private final List<BobGenerationEvent> generationEvents = new CopyOnWriteArrayList<>();
 23 | 
 24 |     /**
 25 |      * Generates a fix snippet based on the error type.
 26 |      *
 27 |      * @param errorType    the type of error to fix
 28 |      * @param originalCode the original problematic code (optional)
 29 |      * @return the generated fix snippet
 30 |      */
 31 |     public String generateFix(String errorType, String originalCode) {
 32 |         String fixSnippet;
 33 | 
 34 |         switch (errorType) {
 35 |             case "NullPointerException" -> fixSnippet = """
 36 |                 if (obj == null) {
 37 |                     log.warn("Null object detected, returning default value");
 38 |                     return;
 39 |                 }
 40 |                 """;
 41 | 
 42 |             case "IndexOutOfBoundsException" -> fixSnippet = """
 43 |                 if (index < 0 || index >= list.size()) {
 44 |                     log.warn("Index out of bounds: {}, list size: {}", index, list.size());
 45 |                     return defaultValue;
 46 |                 }
 47 |                 """;
 48 | 
 49 |             case "ArithmeticException" -> fixSnippet = """
 50 |                 if (denominator == 0) {
 51 |                     log.warn("Division by zero detected, returning 0");
 52 |                     return 0;
 53 |                 }
 54 |                 """;
 55 | 
 56 |             case "DateTimeParseException" -> fixSnippet = """
 57 |                 try {
 58 |                     return parseDefault(date);
 59 |                 } catch (DateTimeParseException e) {
 60 |                     log.warn("Date parse error, using fallback date");
 61 |                     return LocalDateTime.now();
 62 |                 }
 63 |                 """;
 64 | 
 65 |             default -> {
 66 |                 log.warn("Unknown error type: {}", errorType);
 67 |                 fixSnippet = "// No fix available for error type: " + errorType;
 68 |             }
 69 |         }
 70 | 
 71 |         // Record the generation event
 72 |         String timestamp = Instant.now().toString();
 73 |         BobGenerationEvent event = new BobGenerationEvent(
 74 |             timestamp,
 75 |             errorType,
 76 |             fixSnippet,
 77 |             originalCode
 78 |         );
 79 |         generationEvents.add(event);
 80 | 
 81 |         log.info("Generated fix for {}: {} characters", errorType, fixSnippet.length());
 82 | 
 83 |         return fixSnippet;
 84 |     }
 85 | 
 86 |     /**
 87 |      * Returns all recorded generation events for reporting.
 88 |      *
 89 |      * @return list of all Bob generation events
 90 |      */
 91 |     public List<BobGenerationEvent> getBobReport() {
 92 |         return List.copyOf(generationEvents);
 93 |     }
 94 | 
 95 |     /**
 96 |      * Clears all recorded generation events.
 97 |      * Used for testing and reset purposes.
 98 |      */
 99 |     public void clearReport() {
100 |         generationEvents.clear();
101 |         log.info("Bob report cleared");
102 |     }
103 | 
104 |     /**
105 |      * Returns a summary of Bob's activities.
106 |      *
107 |      * @return map containing summary statistics
108 |      */
109 |     public Map<String, Object> getReportSummary() {
110 |         Map<String, Object> summary = new HashMap<>();
111 | 
112 |         // Total fixes
113 |         summary.put("totalFixes", generationEvents.size());
114 | 
115 |         // Fixes by type
116 |         Map<String, Long> fixesByType = generationEvents.stream()
117 |             .collect(Collectors.groupingBy(
118 |                 BobGenerationEvent::errorType,
119 |                 Collectors.counting()
120 |             ));
121 |         summary.put("fixesByType", fixesByType);
122 | 
123 |         // Last fix timestamp
124 |         String lastFixTimestamp = generationEvents.isEmpty()
125 |             ? null
126 |             : generationEvents.get(generationEvents.size() - 1).timestamp();
127 |         summary.put("lastFixTimestamp", lastFixTimestamp);
128 | 
129 |         // Bob version
130 |         summary.put("bobVersion", "1.0.0");
131 | 
132 |         return summary;
133 |     }
134 | }
135 | 
136 | // Made with Bob
</content>
</file>
<file><path>backend/src/main/java/com/hackathon/service/GitHubService.java</path>
<content lines="1-310">
  1 | package com.hackathon.service;
  2 | 
  3 | import lombok.extern.slf4j.Slf4j;
  4 | import okhttp3.*;
  5 | import org.json.JSONObject;
  6 | import org.springframework.beans.factory.annotation.Value;
  7 | import org.springframework.stereotype.Service;
  8 | 
  9 | import java.io.IOException;
 10 | import java.util.Base64;
 11 | 
 12 | @Service
 13 | @Slf4j
 14 | public class GitHubService {
 15 | 
 16 |     @Value("${github.token:}")
 17 |     private String githubToken;
 18 | 
 19 |     private static final String GITHUB_API_BASE = "https://api.github.com";
 20 |     private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
 21 |     private final OkHttpClient client = new OkHttpClient();
 22 | 
 23 |     /**
 24 |      * Creates a pull request with the fixed code
 25 |      *
 26 |      * @param repo         GitHub repository (format: "owner/repo")
 27 |      * @param filePath     path to the file being fixed
 28 |      * @param originalCode the buggy code
 29 |      * @param fixedCode    the fixed code
 30 |      * @param errorType    type of error being fixed
 31 |      * @return PR URL
 32 |      */
 33 |     public String createPullRequest(String repo, String filePath, String originalCode, 
 34 |                                    String fixedCode, String errorType) {
 35 |         long timestamp = System.currentTimeMillis();
 36 |         String branchName = "prod-fix-" + timestamp;
 37 | 
 38 |         // Handle missing GitHub token
 39 |         if (githubToken == null || githubToken.trim().isEmpty()) {
 40 |             log.warn("GitHub token not configured. Returning mock PR URL for demo purposes.");
 41 |             return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
 42 |         }
 43 | 
 44 |         try {
 45 |             log.info("Creating pull request for repo: {}, file: {}, error: {}", repo, filePath, errorType);
 46 | 
 47 |             // Get default branch and latest commit
 48 |             String defaultBranch = getDefaultBranch(repo);
 49 |             if (defaultBranch == null) {
 50 |                 log.error("Failed to get default branch for repo: {}", repo);
 51 |                 return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
 52 |             }
 53 | 
 54 |             String latestCommitSha = getLatestCommitSha(repo, defaultBranch);
 55 |             if (latestCommitSha == null) {
 56 |                 log.error("Failed to get latest commit SHA for repo: {}", repo);
 57 |                 return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
 58 |             }
 59 | 
 60 |             // Create new branch
 61 |             String branchSha = createBranch(repo, branchName, latestCommitSha);
 62 |             if (branchSha == null) {
 63 |                 log.error("Failed to create branch: {}", branchName);
 64 |                 return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
 65 |             }
 66 | 
 67 |             // Commit fixed code to new branch
 68 |             String commitMessage = String.format("Auto-fix by IBM Bob: %s", errorType);
 69 |             String commitSha = commitFile(repo, branchName, filePath, fixedCode, commitMessage);
 70 |             if (commitSha == null) {
 71 |                 log.error("Failed to commit file to branch: {}", branchName);
 72 |                 return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
 73 |             }
 74 | 
 75 |             // Create pull request
 76 |             String prTitle = String.format("Auto-fix by IBM Bob: %s", errorType);
 77 |             String prBody = buildPRBody(errorType, originalCode, fixedCode, timestamp);
 78 |             String prUrl = createPR(repo, branchName, prTitle, prBody);
 79 | 
 80 |             log.info("Successfully created pull request: {}", prUrl);
 81 |             return prUrl;
 82 | 
 83 |         } catch (Exception e) {
 84 |             log.error("Error creating pull request: {}", e.getMessage(), e);
 85 |             return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
 86 |         }
 87 |     }
 88 | 
 89 |     /**
 90 |      * Gets the default branch of a repository
 91 |      */
 92 |     private String getDefaultBranch(String repo) {
 93 |         try {
 94 |             String url = String.format("%s/repos/%s", GITHUB_API_BASE, repo);
 95 |             Request request = new Request.Builder()
 96 |                     .url(url)
 97 |                     .header("Authorization", "Bearer " + githubToken)
 98 |                     .header("Accept", "application/vnd.github.v3+json")
 99 |                     .build();
100 | 
101 |             try (Response response = client.newCall(request).execute()) {
102 |                 if (!response.isSuccessful()) {
103 |                     log.error("Failed to get default branch. Status: {}", response.code());
104 |                     return null;
105 |                 }
106 | 
107 |                 String responseBody = response.body().string();
108 |                 JSONObject json = new JSONObject(responseBody);
109 |                 return json.getString("default_branch");
110 |             }
111 |         } catch (Exception e) {
112 |             log.error("Error getting default branch: {}", e.getMessage(), e);
113 |             return null;
114 |         }
115 |     }
116 | 
117 |     /**
118 |      * Gets the latest commit SHA for a branch
119 |      */
120 |     private String getLatestCommitSha(String repo, String branch) {
121 |         try {
122 |             String url = String.format("%s/repos/%s/git/refs/heads/%s", GITHUB_API_BASE, repo, branch);
123 |             Request request = new Request.Builder()
124 |                     .url(url)
125 |                     .header("Authorization", "Bearer " + githubToken)
126 |                     .header("Accept", "application/vnd.github.v3+json")
127 |                     .build();
128 | 
129 |             try (Response response = client.newCall(request).execute()) {
130 |                 if (!response.isSuccessful()) {
131 |                     log.error("Failed to get latest commit SHA. Status: {}", response.code());
132 |                     return null;
133 |                 }
134 | 
135 |                 String responseBody = response.body().string();
136 |                 JSONObject json = new JSONObject(responseBody);
137 |                 return json.getJSONObject("object").getString("sha");
138 |             }
139 |         } catch (Exception e) {
140 |             log.error("Error getting latest commit SHA: {}", e.getMessage(), e);
141 |             return null;
142 |         }
143 |     }
144 | 
145 |     /**
146 |      * Creates a new branch in GitHub
147 |      *
148 |      * @param repo       GitHub repository
149 |      * @param branchName name of the new branch
150 |      * @param baseSha    SHA of the commit to branch from
151 |      * @return branch SHA or null on error
152 |      */
153 |     private String createBranch(String repo, String branchName, String baseSha) {
154 |         try {
155 |             String url = String.format("%s/repos/%s/git/refs", GITHUB_API_BASE, repo);
156 |             
157 |             JSONObject payload = new JSONObject();
158 |             payload.put("ref", "refs/heads/" + branchName);
159 |             payload.put("sha", baseSha);
160 | 
161 |             RequestBody body = RequestBody.create(payload.toString(), JSON);
162 |             Request request = new Request.Builder()
163 |                     .url(url)
164 |                     .post(body)
165 |                     .header("Authorization", "Bearer " + githubToken)
166 |                     .header("Accept", "application/vnd.github.v3+json")
167 |                     .build();
168 | 
169 |             try (Response response = client.newCall(request).execute()) {
170 |                 if (!response.isSuccessful()) {
171 |                     log.error("Failed to create branch. Status: {}, Body: {}", 
172 |                              response.code(), response.body().string());
173 |                     return null;
174 |                 }
175 | 
176 |                 String responseBody = response.body().string();
177 |                 JSONObject json = new JSONObject(responseBody);
178 |                 log.info("Successfully created branch: {}", branchName);
179 |                 return json.getJSONObject("object").getString("sha");
180 |             }
181 |         } catch (Exception e) {
182 |             log.error("Error creating branch: {}", e.getMessage(), e);
183 |             return null;
184 |         }
185 |     }
186 | 
187 |     /**
188 |      * Commits a file to the specified branch
189 |      *
190 |      * @param repo       GitHub repository
191 |      * @param branchName branch to commit to
192 |      * @param filePath   path to the file
193 |      * @param content    file content
194 |      * @param message    commit message
195 |      * @return commit SHA or null on error
196 |      */
197 |     private String commitFile(String repo, String branchName, String filePath, 
198 |                              String content, String message) {
199 |         try {
200 |             String url = String.format("%s/repos/%s/contents/%s", GITHUB_API_BASE, repo, filePath);
201 |             
202 |             // Encode content to base64
203 |             String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());
204 |             
205 |             JSONObject payload = new JSONObject();
206 |             payload.put("message", message);
207 |             payload.put("content", encodedContent);
208 |             payload.put("branch", branchName);
209 | 
210 |             RequestBody body = RequestBody.create(payload.toString(), JSON);
211 |             Request request = new Request.Builder()
212 |                     .url(url)
213 |                     .put(body)
214 |                     .header("Authorization", "Bearer " + githubToken)
215 |                     .header("Accept", "application/vnd.github.v3+json")
216 |                     .build();
217 | 
218 |             try (Response response = client.newCall(request).execute()) {
219 |                 if (!response.isSuccessful()) {
220 |                     log.error("Failed to commit file. Status: {}, Body: {}", 
221 |                              response.code(), response.body().string());
222 |                     return null;
223 |                 }
224 | 
225 |                 String responseBody = response.body().string();
226 |                 JSONObject json = new JSONObject(responseBody);
227 |                 log.info("Successfully committed file: {} to branch: {}", filePath, branchName);
228 |                 return json.getJSONObject("commit").getString("sha");
229 |             }
230 |         } catch (Exception e) {
231 |             log.error("Error committing file: {}", e.getMessage(), e);
232 |             return null;
233 |         }
234 |     }
235 | 
236 |     /**
237 |      * Creates a pull request
238 |      *
239 |      * @param repo       GitHub repository
240 |      * @param branchName source branch
241 |      * @param title      PR title
242 |      * @param body       PR body
243 |      * @return PR URL or mock URL on error
244 |      */
245 |     private String createPR(String repo, String branchName, String title, String body) {
246 |         try {
247 |             String url = String.format("%s/repos/%s/pulls", GITHUB_API_BASE, repo);
248 |             
249 |             // Get default branch for base
250 |             String defaultBranch = getDefaultBranch(repo);
251 |             if (defaultBranch == null) {
252 |                 defaultBranch = "main";
253 |             }
254 |             
255 |             JSONObject payload = new JSONObject();
256 |             payload.put("title", title);
257 |             payload.put("body", body);
258 |             payload.put("head", branchName);
259 |             payload.put("base", defaultBranch);
260 | 
261 |             RequestBody requestBody = RequestBody.create(payload.toString(), JSON);
262 |             Request request = new Request.Builder()
263 |                     .url(url)
264 |                     .post(requestBody)
265 |                     .header("Authorization", "Bearer " + githubToken)
266 |                     .header("Accept", "application/vnd.github.v3+json")
267 |                     .build();
268 | 
269 |             try (Response response = client.newCall(request).execute()) {
270 |                 if (!response.isSuccessful()) {
271 |                     log.error("Failed to create PR. Status: {}, Body: {}", 
272 |                              response.code(), response.body().string());
273 |                     return String.format("https://github.com/%s/pull/mock-pr-%d", 
274 |                                        repo, System.currentTimeMillis());
275 |                 }
276 | 
277 |                 String responseBody = response.body().string();
278 |                 JSONObject json = new JSONObject(responseBody);
279 |                 String prUrl = json.getString("html_url");
280 |                 log.info("Successfully created PR: {}", prUrl);
281 |                 return prUrl;
282 |             }
283 |         } catch (Exception e) {
284 |             log.error("Error creating PR: {}", e.getMessage(), e);
285 |             return String.format("https://github.com/%s/pull/mock-pr-%d", 
286 |                                repo, System.currentTimeMillis());
287 |         }
288 |     }
289 | 
290 |     /**
291 |      * Builds the PR body with error details and code snippets
292 |      */
293 |     private String buildPRBody(String errorType, String originalCode, 
294 |                               String fixedCode, long timestamp) {
295 |         return String.format(
296 |             "## Auto-fix by IBM Bob\n\n" +
297 |             "**Error Type:** %s\n\n" +
298 |             "**Timestamp:** %d\n\n" +
299 |             "### Original Code\n" +
300 |             "```java\n%s\n```\n\n" +
301 |             "### Fixed Code\n" +
302 |             "```java\n%s\n```\n\n" +
303 |             "---\n" +
304 |             "*This PR was automatically generated by IBM Bob Code Generator*",
305 |             errorType, timestamp, originalCode, fixedCode
306 |         );
307 |     }
308 | }
309 | 
310 | // Made with Bob
</content>
</file>
<file><path>backend/src/main/java/com/hackathon/service/SlackNotifier.java</path>
<content lines="1-193">
  1 | package com.hackathon.service;
  2 | 
  3 | import lombok.extern.slf4j.Slf4j;
  4 | import okhttp3.*;
  5 | import org.json.JSONArray;
  6 | import org.json.JSONObject;
  7 | import org.springframework.beans.factory.annotation.Value;
  8 | import org.springframework.stereotype.Service;
  9 | 
 10 | import java.io.IOException;
 11 | import java.time.Instant;
 12 | import java.time.ZoneId;
 13 | import java.time.format.DateTimeFormatter;
 14 | 
 15 | @Service
 16 | @Slf4j
 17 | public class SlackNotifier {
 18 | 
 19 |     @Value("${slack.webhook.url:}")
 20 |     private String slackWebhookUrl;
 21 | 
 22 |     private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
 23 |     private final OkHttpClient client = new OkHttpClient();
 24 | 
 25 |     /**
 26 |      * Sends a notification to Slack when Bob generates a fix
 27 |      *
 28 |      * @param errorType   type of error that was fixed
 29 |      * @param fixSnippet  the generated fix code snippet
 30 |      * @param prUrl       URL of the created pull request
 31 |      * @return true if sent successfully, false otherwise
 32 |      */
 33 |     public boolean sendNotification(String errorType, String fixSnippet, String prUrl) {
 34 |         // Check if Slack webhook URL is configured
 35 |         if (slackWebhookUrl == null || slackWebhookUrl.trim().isEmpty()) {
 36 |             log.info("SLACK NOTIFICATION (Console Mode): Error: {}, PR: {}", errorType, prUrl);
 37 |             return true; // Return true for console mode (demo purposes)
 38 |         }
 39 | 
 40 |         try {
 41 |             log.info("Sending Slack notification for error type: {}", errorType);
 42 |             
 43 |             // Format the Slack message
 44 |             String message = formatSlackMessage(errorType, fixSnippet, prUrl);
 45 |             
 46 |             // Send the message
 47 |             boolean success = sendMessage(message);
 48 |             
 49 |             if (success) {
 50 |                 log.info("Successfully sent Slack notification for error: {}", errorType);
 51 |             } else {
 52 |                 log.error("Failed to send Slack notification for error: {}", errorType);
 53 |             }
 54 |             
 55 |             return success;
 56 |             
 57 |         } catch (Exception e) {
 58 |             log.error("Error sending Slack notification: {}", e.getMessage(), e);
 59 |             return false;
 60 |         }
 61 |     }
 62 | 
 63 |     /**
 64 |      * Formats a Slack message using Block Kit format
 65 |      *
 66 |      * @param errorType  type of error that was fixed
 67 |      * @param fixSnippet the generated fix code snippet
 68 |      * @param prUrl      URL of the created pull request
 69 |      * @return formatted JSON string for Slack
 70 |      */
 71 |     private String formatSlackMessage(String errorType, String fixSnippet, String prUrl) {
 72 |         // Get current timestamp in ISO format
 73 |         String timestamp = DateTimeFormatter.ISO_INSTANT
 74 |                 .withZone(ZoneId.of("UTC"))
 75 |                 .format(Instant.now());
 76 | 
 77 |         // Escape special characters in the fix snippet for JSON
 78 |         String escapedFixSnippet = escapeJsonString(fixSnippet);
 79 | 
 80 |         // Build the Slack message using Block Kit format
 81 |         JSONObject message = new JSONObject();
 82 |         message.put("text", "IBM Bob Auto-Fix Alert");
 83 | 
 84 |         JSONArray blocks = new JSONArray();
 85 | 
 86 |         // Header block
 87 |         JSONObject headerBlock = new JSONObject();
 88 |         headerBlock.put("type", "header");
 89 |         JSONObject headerText = new JSONObject();
 90 |         headerText.put("type", "plain_text");
 91 |         headerText.put("text", "🤖 IBM Bob Auto-Fix Alert");
 92 |         headerBlock.put("text", headerText);
 93 |         blocks.put(headerBlock);
 94 | 
 95 |         // Error type and status section
 96 |         JSONObject errorSection = new JSONObject();
 97 |         errorSection.put("type", "section");
 98 |         JSONObject errorText = new JSONObject();
 99 |         errorText.put("type", "mrkdwn");
100 |         errorText.put("text", String.format("*Error Type:* %s\n*Status:* Fix Generated ✅", errorType));
101 |         errorSection.put("text", errorText);
102 |         blocks.put(errorSection);
103 | 
104 |         // Fix snippet section
105 |         JSONObject fixSection = new JSONObject();
106 |         fixSection.put("type", "section");
107 |         JSONObject fixText = new JSONObject();
108 |         fixText.put("type", "mrkdwn");
109 |         fixText.put("text", String.format("*Generated Fix:*\n```%s```", escapedFixSnippet));
110 |         fixSection.put("text", fixText);
111 |         blocks.put(fixSection);
112 | 
113 |         // Pull request section
114 |         JSONObject prSection = new JSONObject();
115 |         prSection.put("type", "section");
116 |         JSONObject prText = new JSONObject();
117 |         prText.put("type", "mrkdwn");
118 |         prText.put("text", String.format("*Pull Request:* <%s|View PR>", prUrl));
119 |         prSection.put("text", prText);
120 |         blocks.put(prSection);
121 | 
122 |         // Context footer with mentions and timestamp
123 |         JSONObject contextBlock = new JSONObject();
124 |         contextBlock.put("type", "context");
125 |         JSONArray contextElements = new JSONArray();
126 |         JSONObject contextText = new JSONObject();
127 |         contextText.put("type", "mrkdwn");
128 |         contextText.put("text", String.format("cc: @developer @lead | %s", timestamp));
129 |         contextElements.put(contextText);
130 |         contextBlock.put("elements", contextElements);
131 |         blocks.put(contextBlock);
132 | 
133 |         message.put("blocks", blocks);
134 | 
135 |         return message.toString();
136 |     }
137 | 
138 |     /**
139 |      * Sends a formatted message to Slack
140 |      *
141 |      * @param message the formatted JSON message
142 |      * @return true if successful, false otherwise
143 |      */
144 |     private boolean sendMessage(String message) {
145 |         try {
146 |             RequestBody body = RequestBody.create(message, JSON);
147 |             Request request = new Request.Builder()
148 |                     .url(slackWebhookUrl)
149 |                     .post(body)
150 |                     .build();
151 | 
152 |             try (Response response = client.newCall(request).execute()) {
153 |                 if (!response.isSuccessful()) {
154 |                     log.error("Failed to send Slack message. Status: {}, Body: {}", 
155 |                              response.code(), response.body() != null ? response.body().string() : "null");
156 |                     return false;
157 |                 }
158 | 
159 |                 log.debug("Slack API response: {}", response.body() != null ? response.body().string() : "ok");
160 |                 return true;
161 |             }
162 |         } catch (IOException e) {
163 |             log.error("IOException while sending Slack message: {}", e.getMessage(), e);
164 |             return false;
165 |         } catch (Exception e) {
166 |             log.error("Unexpected error while sending Slack message: {}", e.getMessage(), e);
167 |             return false;
168 |         }
169 |     }
170 | 
171 |     /**
172 |      * Escapes special characters in a string for JSON
173 |      *
174 |      * @param input the string to escape
175 |      * @return escaped string
176 |      */
177 |     private String escapeJsonString(String input) {
178 |         if (input == null) {
179 |             return "";
180 |         }
181 |         
182 |         return input
183 |                 .replace("\\", "\\\\")
184 |                 .replace("\"", "\\\"")
185 |                 .replace("\n", "\\n")
186 |                 .replace("\r", "\\r")
187 |                 .replace("\t", "\\t")
188 |                 .replace("\b", "\\b")
189 |                 .replace("\f", "\\f");
190 |     }
191 | }
192 | 
193 | // Made with Bob
</content>
</file>
<file><path>backend/src/main/java/com/hackathon/service/PlaywrightTestRunner.java</path>
<content lines="1-358">
  1 | package com.hackathon.service;
  2 | 
  3 | import com.hackathon.model.TestResult;
  4 | import lombok.extern.slf4j.Slf4j;
  5 | import org.springframework.stereotype.Service;
  6 | 
  7 | import java.io.BufferedReader;
  8 | import java.io.File;
  9 | import java.io.IOException;
 10 | import java.io.InputStreamReader;
 11 | import java.time.Instant;
 12 | import java.time.LocalDateTime;
 13 | import java.time.format.DateTimeFormatter;
 14 | import java.util.ArrayList;
 15 | import java.util.List;
 16 | import java.util.concurrent.TimeUnit;
 17 | import java.util.regex.Matcher;
 18 | import java.util.regex.Pattern;
 19 | 
 20 | /**
 21 |  * Service for running Playwright tests and parsing results.
 22 |  */
 23 | @Service
 24 | @Slf4j
 25 | public class PlaywrightTestRunner {
 26 | 
 27 |     private static final int TEST_TIMEOUT_SECONDS = 60;
 28 |     private static final String TESTS_DIR = "tests";
 29 | 
 30 |     /**
 31 |      * Runs a specific Playwright test file.
 32 |      * 
 33 |      * @param testFilePath path to the test file to run
 34 |      * @return TestResult containing test execution details
 35 |      */
 36 |     public TestResult runTest(String testFilePath) {
 37 |         long startTime = System.currentTimeMillis();
 38 |         String timestamp = Instant.now().toString();
 39 | 
 40 |         try {
 41 |             // Validate test file exists
 42 |             File testFile = new File(testFilePath);
 43 |             if (!testFile.exists()) {
 44 |                 log.error("Test file not found: {}", testFilePath);
 45 |                 return new TestResult(
 46 |                     false,
 47 |                     testFilePath,
 48 |                     "Error: Test file not found",
 49 |                     0,
 50 |                     timestamp
 51 |                 );
 52 |             }
 53 | 
 54 |             // Build command
 55 |             String command = String.format("npx playwright test %s", testFilePath);
 56 |             log.info("Executing Playwright test command: {}", command);
 57 | 
 58 |             // Execute command using ProcessBuilder
 59 |             ProcessBuilder processBuilder = new ProcessBuilder();
 60 |             
 61 |             // Handle Windows vs Linux command execution
 62 |             if (System.getProperty("os.name").toLowerCase().contains("windows")) {
 63 |                 processBuilder.command("cmd.exe", "/c", command);
 64 |             } else {
 65 |                 processBuilder.command("sh", "-c", command);
 66 |             }
 67 | 
 68 |             processBuilder.directory(new File(System.getProperty("user.dir")));
 69 |             processBuilder.redirectErrorStream(true);
 70 | 
 71 |             Process process = processBuilder.start();
 72 | 
 73 |             // Capture output
 74 |             StringBuilder output = new StringBuilder();
 75 |             try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
 76 |                 String line;
 77 |                 while ((line = reader.readLine()) != null) {
 78 |                     output.append(line).append("\n");
 79 |                     log.debug("Test output: {}", line);
 80 |                 }
 81 |             }
 82 | 
 83 |             // Wait for process to complete with timeout
 84 |             boolean completed = process.waitFor(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
 85 |             
 86 |             if (!completed) {
 87 |                 process.destroyForcibly();
 88 |                 long duration = System.currentTimeMillis() - startTime;
 89 |                 log.error("Playwright test timed out after {} seconds", TEST_TIMEOUT_SECONDS);
 90 |                 return new TestResult(
 91 |                     false,
 92 |                     testFilePath,
 93 |                     "Error: Test execution timed out after " + TEST_TIMEOUT_SECONDS + " seconds",
 94 |                     (int) duration,
 95 |                     timestamp
 96 |                 );
 97 |             }
 98 | 
 99 |             int exitCode = process.exitValue();
100 |             long duration = System.currentTimeMillis() - startTime;
101 |             String outputStr = output.toString();
102 | 
103 |             // Parse output to determine if test passed
104 |             boolean passed = exitCode == 0;
105 |             
106 |             log.info("Playwright test completed. Exit code: {}, Duration: {}ms, Passed: {}", 
107 |                      exitCode, duration, passed);
108 | 
109 |             return new TestResult(
110 |                 passed,
111 |                 testFilePath,
112 |                 outputStr,
113 |                 (int) duration,
114 |                 timestamp
115 |             );
116 | 
117 |         } catch (IOException e) {
118 |             long duration = System.currentTimeMillis() - startTime;
119 |             log.error("IOException while executing Playwright test", e);
120 |             return new TestResult(
121 |                 false,
122 |                 testFilePath,
123 |                 "Error: " + e.getMessage(),
124 |                 (int) duration,
125 |                 timestamp
126 |             );
127 |         } catch (InterruptedException e) {
128 |             long duration = System.currentTimeMillis() - startTime;
129 |             log.error("Interrupted while executing Playwright test", e);
130 |             Thread.currentThread().interrupt();
131 |             return new TestResult(
132 |                 false,
133 |                 testFilePath,
134 |                 "Error: Process interrupted",
135 |                 (int) duration,
136 |                 timestamp
137 |             );
138 |         } catch (Exception e) {
139 |             long duration = System.currentTimeMillis() - startTime;
140 |             log.error("Unexpected error during Playwright test execution", e);
141 |             return new TestResult(
142 |                 false,
143 |                 testFilePath,
144 |                 "Error: " + e.getMessage(),
145 |                 (int) duration,
146 |                 timestamp
147 |             );
148 |         }
149 |     }
150 | 
151 |     /**
152 |      * Runs all Playwright tests in the tests/ directory.
153 |      * 
154 |      * @return List of TestResult objects for all tests
155 |      */
156 |     public List<TestResult> runAllTests() {
157 |         long startTime = System.currentTimeMillis();
158 |         String timestamp = Instant.now().toString();
159 |         List<TestResult> results = new ArrayList<>();
160 | 
161 |         try {
162 |             // Check if tests directory exists
163 |             File testsDir = new File(TESTS_DIR);
164 |             if (!testsDir.exists() || !testsDir.isDirectory()) {
165 |                 log.warn("Tests directory not found: {}", TESTS_DIR);
166 |                 results.add(new TestResult(
167 |                     false,
168 |                     "all-tests",
169 |                     "Error: Tests directory not found",
170 |                     0,
171 |                     timestamp
172 |                 ));
173 |                 return results;
174 |             }
175 | 
176 |             // Build command
177 |             String command = "npx playwright test";
178 |             log.info("Executing Playwright test command: {}", command);
179 | 
180 |             // Execute command using ProcessBuilder
181 |             ProcessBuilder processBuilder = new ProcessBuilder();
182 |             
183 |             // Handle Windows vs Linux command execution
184 |             if (System.getProperty("os.name").toLowerCase().contains("windows")) {
185 |                 processBuilder.command("cmd.exe", "/c", command);
186 |             } else {
187 |                 processBuilder.command("sh", "-c", command);
188 |             }
189 | 
190 |             processBuilder.directory(new File(System.getProperty("user.dir")));
191 |             processBuilder.redirectErrorStream(true);
192 | 
193 |             Process process = processBuilder.start();
194 | 
195 |             // Capture output
196 |             StringBuilder output = new StringBuilder();
197 |             try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
198 |                 String line;
199 |                 while ((line = reader.readLine()) != null) {
200 |                     output.append(line).append("\n");
201 |                     log.debug("Test output: {}", line);
202 |                 }
203 |             }
204 | 
205 |             // Wait for process to complete with timeout
206 |             boolean completed = process.waitFor(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
207 |             
208 |             if (!completed) {
209 |                 process.destroyForcibly();
210 |                 long duration = System.currentTimeMillis() - startTime;
211 |                 log.error("Playwright tests timed out after {} seconds", TEST_TIMEOUT_SECONDS);
212 |                 results.add(new TestResult(
213 |                     false,
214 |                     "all-tests",
215 |                     "Error: Test execution timed out after " + TEST_TIMEOUT_SECONDS + " seconds",
216 |                     (int) duration,
217 |                     timestamp
218 |                 ));
219 |                 return results;
220 |             }
221 | 
222 |             int exitCode = process.exitValue();
223 |             long duration = System.currentTimeMillis() - startTime;
224 |             String outputStr = output.toString();
225 | 
226 |             // Parse output for multiple test results
227 |             List<TestResult> parsedResults = parseTestOutput(outputStr);
228 |             
229 |             if (parsedResults.isEmpty()) {
230 |                 // If no tests were parsed, return a single result
231 |                 boolean passed = exitCode == 0;
232 |                 results.add(new TestResult(
233 |                     passed,
234 |                     "all-tests",
235 |                     outputStr,
236 |                     (int) duration,
237 |                     timestamp
238 |                 ));
239 |             } else {
240 |                 results.addAll(parsedResults);
241 |             }
242 | 
243 |             log.info("Playwright tests completed. Exit code: {}, Duration: {}ms, Tests: {}", 
244 |                      exitCode, duration, results.size());
245 | 
246 |             return results;
247 | 
248 |         } catch (IOException e) {
249 |             long duration = System.currentTimeMillis() - startTime;
250 |             log.error("IOException while executing Playwright tests", e);
251 |             results.add(new TestResult(
252 |                 false,
253 |                 "all-tests",
254 |                 "Error: " + e.getMessage(),
255 |                 (int) duration,
256 |                 timestamp
257 |             ));
258 |             return results;
259 |         } catch (InterruptedException e) {
260 |             long duration = System.currentTimeMillis() - startTime;
261 |             log.error("Interrupted while executing Playwright tests", e);
262 |             Thread.currentThread().interrupt();
263 |             results.add(new TestResult(
264 |                 false,
265 |                 "all-tests",
266 |                 "Error: Process interrupted",
267 |                 (int) duration,
268 |                 timestamp
269 |             ));
270 |             return results;
271 |         } catch (Exception e) {
272 |             long duration = System.currentTimeMillis() - startTime;
273 |             log.error("Unexpected error during Playwright tests execution", e);
274 |             results.add(new TestResult(
275 |                 false,
276 |                 "all-tests",
277 |                 "Error: " + e.getMessage(),
278 |                 (int) duration,
279 |                 timestamp
280 |             ));
281 |             return results;
282 |         }
283 |     }
284 | 
285 |     /**
286 |      * Parses Playwright test output to extract individual test results.
287 |      * 
288 |      * @param output the raw test output
289 |      * @return List of TestResult objects parsed from the output
290 |      */
291 |     private List<TestResult> parseTestOutput(String output) {
292 |         List<TestResult> results = new ArrayList<>();
293 |         String timestamp = Instant.now().toString();
294 | 
295 |         try {
296 |             // Pattern to match test results in Playwright output
297 |             // Example: "✓ [chromium] › test.spec.ts:3:1 › test name (1.2s)"
298 |             // Example: "✗ [chromium] › test.spec.ts:3:1 › test name (1.2s)"
299 |             Pattern testPattern = Pattern.compile("([✓✗×])\\s+\\[.*?\\]\\s+›\\s+(.*?)\\s+›\\s+(.*?)\\s+\\((\\d+(?:\\.\\d+)?)(ms|s)\\)");
300 |             Matcher matcher = testPattern.matcher(output);
301 | 
302 |             while (matcher.find()) {
303 |                 String status = matcher.group(1);
304 |                 String testFile = matcher.group(2);
305 |                 String testName = matcher.group(3);
306 |                 String durationStr = matcher.group(4);
307 |                 String unit = matcher.group(5);
308 | 
309 |                 // Convert duration to milliseconds
310 |                 int duration;
311 |                 if ("s".equals(unit)) {
312 |                     duration = (int) (Double.parseDouble(durationStr) * 1000);
313 |                 } else {
314 |                     duration = (int) Double.parseDouble(durationStr);
315 |                 }
316 | 
317 |                 boolean passed = "✓".equals(status);
318 |                 String fullTestName = testFile + " › " + testName;
319 | 
320 |                 results.add(new TestResult(
321 |                     passed,
322 |                     fullTestName,
323 |                     output,
324 |                     duration,
325 |                     timestamp
326 |                 ));
327 | 
328 |                 log.debug("Parsed test result: {} - {} ({}ms)", passed ? "PASSED" : "FAILED", fullTestName, duration);
329 |             }
330 | 
331 |             // Alternative pattern for simpler output format
332 |             if (results.isEmpty()) {
333 |                 Pattern simplePattern = Pattern.compile("(\\d+)\\s+passed.*?(\\d+)\\s+failed");
334 |                 Matcher simpleMatcher = simplePattern.matcher(output);
335 |                 
336 |                 if (simpleMatcher.find()) {
337 |                     int passed = Integer.parseInt(simpleMatcher.group(1));
338 |                     int failed = Integer.parseInt(simpleMatcher.group(2));
339 |                     
340 |                     results.add(new TestResult(
341 |                         failed == 0,
342 |                         String.format("Test Suite (%d passed, %d failed)", passed, failed),
343 |                         output,
344 |                         0,
345 |                         timestamp
346 |                     ));
347 |                 }
348 |             }
349 | 
350 |         } catch (Exception e) {
351 |             log.error("Error parsing test output", e);
352 |         }
353 | 
354 |         return results;
355 |     }
356 | }
357 | 
358 | // Made with Bob
</content>
</file>
</files>
<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\service\PlaywrightTestRunner.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java,backend/src/main/java/com/hackathon/service/GitHubService.java,backend/src/main/java/com/hackathon/service/SlackNotifier.java,backend/src/main/java/com/hackathon/model/TestResult.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:40:58.857Z
User time zone: Asia/Karachi, UTC+5:00

# Current Cost
$0.01

# Review Exclusions
The following file patterns are excluded from code reviews:
- .vscode/**

# Current Mode
<slug>code</slug>
<name>💻 Code</name>
You have not created a todo list yet. Create one with `update_todo_list` if your task is complicated or involves multiple steps.
</environment_details>

---

**Assistant:**

Now let me read the PlaywrightCodegenService to understand its interface:

<read_file>
<args>
<file>
<path>backend/src/main/java/com/hackathon/service/PlaywrightCodegenService.java</path>
</file>
</args>
</read_file>

---

**User:**

[read_file for 'backend/src/main/java/com/hackathon/service/PlaywrightCodegenService.java'. Reading multiple files at once is more efficient for the LLM. If other files are relevant to your current task, please read them simultaneously.] Result:
<files>
<file><path>backend/src/main/java/com/hackathon/service/PlaywrightCodegenService.java</path>
<content lines="1-162">
  1 | package com.hackathon.service;
  2 | 
  3 | import lombok.extern.slf4j.Slf4j;
  4 | import org.springframework.stereotype.Service;
  5 | 
  6 | import java.io.BufferedReader;
  7 | import java.io.File;
  8 | import java.io.IOException;
  9 | import java.io.InputStreamReader;
 10 | import java.time.LocalDateTime;
 11 | import java.time.format.DateTimeFormatter;
 12 | import java.util.concurrent.TimeUnit;
 13 | 
 14 | /**
 15 |  * Service for generating Playwright test code using codegen.
 16 |  */
 17 | @Service
 18 | @Slf4j
 19 | public class PlaywrightCodegenService {
 20 | 
 21 |     private static final String TESTS_DIR = "tests";
 22 |     private static final int CODEGEN_TIMEOUT_SECONDS = 30;
 23 | 
 24 |     /**
 25 |      * Generates a Playwright test by recording user interactions.
 26 |      * 
 27 |      * @param url URL to record (default: http://localhost:4200)
 28 |      * @return path to the generated test file
 29 |      */
 30 |     public String generateTest(String url) {
 31 |         if (url == null || url.isEmpty()) {
 32 |             url = "http://localhost:4200";
 33 |         }
 34 | 
 35 |         try {
 36 |             // Create tests directory if not exists
 37 |             File testsDir = new File(TESTS_DIR);
 38 |             if (!testsDir.exists()) {
 39 |                 boolean created = testsDir.mkdirs();
 40 |                 if (created) {
 41 |                     log.info("Created tests directory: {}", TESTS_DIR);
 42 |                 }
 43 |             }
 44 | 
 45 |             // Generate output filename with timestamp
 46 |             String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
 47 |             String filename = String.format("%s/recorded-test-%s.spec.ts", TESTS_DIR, timestamp);
 48 | 
 49 |             // Build command
 50 |             String command = String.format("npx playwright codegen %s --output %s", url, filename);
 51 |             log.info("Executing Playwright codegen command: {}", command);
 52 | 
 53 |             // Execute command using ProcessBuilder
 54 |             ProcessBuilder processBuilder = new ProcessBuilder();
 55 |             
 56 |             // Handle Windows vs Linux command execution
 57 |             if (System.getProperty("os.name").toLowerCase().contains("windows")) {
 58 |                 processBuilder.command("cmd.exe", "/c", command);
 59 |             } else {
 60 |                 processBuilder.command("sh", "-c", command);
 61 |             }
 62 | 
 63 |             processBuilder.directory(new File(System.getProperty("user.dir")));
 64 |             processBuilder.redirectErrorStream(true);
 65 | 
 66 |             Process process = processBuilder.start();
 67 | 
 68 |             // Capture output
 69 |             StringBuilder output = new StringBuilder();
 70 |             try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
 71 |                 String line;
 72 |                 while ((line = reader.readLine()) != null) {
 73 |                     output.append(line).append("\n");
 74 |                     log.debug("Codegen output: {}", line);
 75 |                 }
 76 |             }
 77 | 
 78 |             // Wait for process to complete with timeout
 79 |             boolean completed = process.waitFor(CODEGEN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
 80 |             
 81 |             if (!completed) {
 82 |                 process.destroyForcibly();
 83 |                 log.error("Playwright codegen timed out after {} seconds", CODEGEN_TIMEOUT_SECONDS);
 84 |                 return "Error: Command timed out";
 85 |             }
 86 | 
 87 |             int exitCode = process.exitValue();
 88 |             if (exitCode == 0) {
 89 |                 log.info("Playwright codegen completed successfully. Test file: {}", filename);
 90 |                 return filename;
 91 |             } else {
 92 |                 log.error("Playwright codegen failed with exit code: {}. Output: {}", exitCode, output);
 93 |                 return "Error: Command failed with exit code " + exitCode;
 94 |             }
 95 | 
 96 |         } catch (IOException e) {
 97 |             log.error("IOException while executing Playwright codegen", e);
 98 |             return "Error: " + e.getMessage();
 99 |         } catch (InterruptedException e) {
100 |             log.error("Interrupted while executing Playwright codegen", e);
101 |             Thread.currentThread().interrupt();
102 |             return "Error: Process interrupted";
103 |         } catch (Exception e) {
104 |             log.error("Unexpected error during Playwright codegen", e);
105 |             return "Error: " + e.getMessage();
106 |         }
107 |     }
108 | 
109 |     /**
110 |      * Checks if Playwright is installed.
111 |      * 
112 |      * @return true if Playwright is installed, false otherwise
113 |      */
114 |     public boolean isPlaywrightInstalled() {
115 |         try {
116 |             ProcessBuilder processBuilder = new ProcessBuilder();
117 |             
118 |             // Handle Windows vs Linux command execution
119 |             if (System.getProperty("os.name").toLowerCase().contains("windows")) {
120 |                 processBuilder.command("cmd.exe", "/c", "npx playwright --version");
121 |             } else {
122 |                 processBuilder.command("sh", "-c", "npx playwright --version");
123 |             }
124 | 
125 |             processBuilder.redirectErrorStream(true);
126 |             Process process = processBuilder.start();
127 | 
128 |             // Wait for process to complete with short timeout
129 |             boolean completed = process.waitFor(5, TimeUnit.SECONDS);
130 |             
131 |             if (!completed) {
132 |                 process.destroyForcibly();
133 |                 log.warn("Playwright version check timed out");
134 |                 return false;
135 |             }
136 | 
137 |             int exitCode = process.exitValue();
138 |             boolean installed = exitCode == 0;
139 |             
140 |             if (installed) {
141 |                 log.info("Playwright is installed");
142 |             } else {
143 |                 log.warn("Playwright is not installed or not accessible");
144 |             }
145 |             
146 |             return installed;
147 | 
148 |         } catch (IOException e) {
149 |             log.error("IOException while checking Playwright installation", e);
150 |             return false;
151 |         } catch (InterruptedException e) {
152 |             log.error("Interrupted while checking Playwright installation", e);
153 |             Thread.currentThread().interrupt();
154 |             return false;
155 |         } catch (Exception e) {
156 |             log.error("Unexpected error while checking Playwright installation", e);
157 |             return false;
158 |         }
159 |     }
160 | }
161 | 
162 | // Made with Bob
</content>
</file>
</files>
<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\service\PlaywrightTestRunner.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java,backend/src/main/java/com/hackathon/service/GitHubService.java,backend/src/main/java/com/hackathon/service/SlackNotifier.java,backend/src/main/java/com/hackathon/model/TestResult.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:41:06.113Z
User time zone: Asia/Karachi, UTC+5:00

# Current Cost
$0.07

# Review Exclusions
The following file patterns are excluded from code reviews:
- .vscode/**

# Current Mode
<slug>code</slug>
<name>💻 Code</name>
You have not created a todo list yet. Create one with `update_todo_list` if your task is complicated or involves multiple steps.
</environment_details>

---

**Assistant:**

Now I'll create the AutoFixController with all the required endpoints and orchestration logic:

<write_to_file>
<path>backend/src/main/java/com/hackathon/controller/AutoFixController.java</path>
<content>
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
            String repo = request.getOrDefault("repo", "hackathon-team/flight-dashboard");
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

                    // Create PR (using default repo and file path)
                    String repo = "hackathon-team/flight-dashboard";
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
</content>
<line_count>344</line_count>
</write_to_file>

---

**User:**

[write_to_file for 'backend/src/main/java/com/hackathon/controller/AutoFixController.java'] Result:
<file_write_result>
<path>backend/src/main/java/com/hackathon/controller/AutoFixController.java</path>
<operation>created</operation>
<notice>
<i>You do not need to re-read the file, as you have seen all changes</i>
<i>Proceed with the task using these changes as the new baseline.</i>
</notice>
</file_write_result>

<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\controller\AutoFixController.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java,backend/src/main/java/com/hackathon/service/GitHubService.java,backend/src/main/java/com/hackathon/service/SlackNotifier.java,backend/src/main/java/com/hackathon/model/TestResult.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:42:02.226Z
User time zone: Asia/Karachi, UTC+5:00

# Current Cost
$0.13

# Review Exclusions
The following file patterns are excluded from code reviews:
- .vscode/**

# Current Mode
<slug>code</slug>
<name>💻 Code</name>
You have not created a todo list yet. Create one with `update_todo_list` if your task is complicated or involves multiple steps.
</environment_details>

---

**Assistant:**

<attempt_completion>
<result>
Successfully created AutoFixController.java - the main orchestrator REST controller for the auto-fix workflow.

**Endpoints Created:**

1. **POST /api/auto-fix** - Main auto-fix endpoint
   - Analyzes log line, generates fix, creates GitHub PR, sends Slack notification, and optionally runs tests
   - Returns: fixable, errorType, fixGenerated, fixSnippet, prUrl, slackNotified, testPassed (optional), message

2. **POST /api/record-test** - Record Playwright test
   - Generates Playwright test using codegen for specified URL (defaults to http://localhost:4200)
   - Returns: success, testFilePath, message

3. **GET /api/auto-fix/status** - System status check
   - Checks Playwright installation, GitHub/Slack configuration, and Bob's activity
   - Returns: playwrightInstalled, githubConfigured, slackConfigured, totalFixesGenerated, message

4. **POST /api/auto-fix/batch** - Batch process log file
   - Analyzes entire log file, generates fixes for all fixable errors, creates PRs, and sends batch notification
   - Returns: totalErrors, fixableErrors, fixesGenerated, prsCreated (list of PR URLs), message

**Features:**
- Constructor injection for all 6 services (LogAnalyzerService, BobCodeGenerator, GitHubService, SlackNotifier, PlaywrightTestRunner, PlaywrightCodegenService)
- Comprehensive error handling with try-catch blocks
- Detailed logging throughout the workflow
- Default values for optional parameters (repo, filePath, url)
- CORS enabled for frontend access
- Graceful handling of missing configurations (GitHub token, Slack webhook)
- Step-by-step workflow execution with logging
</result>
</attempt_completion>

