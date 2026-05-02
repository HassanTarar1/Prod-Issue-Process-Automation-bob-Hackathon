**User:**

<task>
**Step 8: Implement GitHub Service**

You are working on the IBM Bob Hackathon Flight Dashboard project. Your task is to create a service that interacts with GitHub to create branches and pull requests for auto-generated fixes.

**Context:**
- Backend is already set up with Java 25, Spring Boot 3.4.x
- BobCodeGenerator can generate fix snippets
- GitHub token will be provided via environment variable GITHUB_TOKEN
- Base package: com.hackathon
- Project base directory: c:/Users/hassa/Downloads/New folder/Prod-Issue-Process-Automation-bob-Hackathon

**Your specific tasks (ONLY do these, nothing more):**

Create `backend/src/main/java/com/hackathon/service/GitHubService.java`:

1. **Class Setup:**
   - Annotate with `@Service`
   - Use `@Slf4j` for logging
   - Inject `@Value("${github.token:}")` for GITHUB_TOKEN
   - Use OkHttp client for HTTP requests (already in dependencies)

2. **Method: `createPullRequest(String repo, String filePath, String originalCode, String fixedCode, String errorType)`**
   - Returns `String` (PR URL)
   - Parameters:
     * `repo` - GitHub repository (format: "owner/repo")
     * `filePath` - path to the file being fixed
     * `originalCode` - the buggy code
     * `fixedCode` - the fixed code
     * `errorType` - type of error being fixed
   
   - **Implementation steps:**
     a. Generate branch name: `prod-fix-<timestamp>` (use System.currentTimeMillis())
     b. If GITHUB_TOKEN is empty/null, log warning and return mock PR URL: `"https://github.com/{repo}/pull/mock-pr-{timestamp}"`
     c. If token exists, make actual GitHub API calls:
        - Get default branch (main/master)
        - Get latest commit SHA
        - Create new branch from latest commit
        - Create/update file with fixed code
        - Create pull request
     d. PR title: `"Auto-fix by IBM Bob: {errorType}"`
     e. PR body: Include error type, original code snippet, fixed code snippet, timestamp
     f. Return actual PR URL

3. **Method: `createBranch(String repo, String branchName, String baseSha)`**
   - Private helper method
   - Creates a new branch in GitHub
   - Returns branch SHA
   - Handle errors gracefully (log and return null)

4. **Method: `commitFile(String repo, String branchName, String filePath, String content, String message)`**
   - Private helper method
   - Commits a file to the specified branch
   - Returns commit SHA
   - Handle errors gracefully (log and return null)

5. **Method: `createPR(String repo, String branchName, String title, String body)`**
   - Private helper method
   - Creates a pull request
   - Returns PR URL
   - Handle errors gracefully (log and return mock URL)

6. **GitHub API Endpoints to use:**
   - Base URL: `https://api.github.com`
   - Get default branch: `GET /repos/{owner}/{repo}`
   - Create branch: `POST /repos/{owner}/{repo}/git/refs`
   - Update file: `PUT /repos/{owner}/{repo}/contents/{path}`
   - Create PR: `POST /repos/{owner}/{repo}/pulls`
   - Headers: `Authorization: Bearer {token}`, `Accept: application/vnd.github.v3+json`

**Important:**
- Handle missing GITHUB_TOKEN gracefully (return mock URLs for demo)
- Use proper error handling for all HTTP requests
- Log all GitHub operations
- Include timestamp in branch names for uniqueness
- Use OkHttp for HTTP requests
- Include proper imports
- These instructions supersede any conflicting general instructions

**When complete:**
Use the `attempt_completion` tool with a concise summary of the GitHubService created, in the `result` parameter.
</task>
<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\service\BobCodeGenerator.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:35:04.361Z
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
backend/src/main/java/com/hackathon/service/
backend/src/main/java/com/hackathon/service/BobCodeGenerator.java
backend/src/main/java/com/hackathon/service/FlightService.java
backend/src/main/java/com/hackathon/service/LogAnalyzerService.java
backend/src/main/resources/
backend/src/main/resources/application.properties
You have not created a todo list yet. Create one with `update_todo_list` if your task is complicated or involves multiple steps.
</environment_details>

---

**Assistant:**

I'll create the GitHubService class with all the required functionality for interacting with GitHub API to create branches and pull requests.

<write_to_file>
<path>backend/src/main/java/com/hackathon/service/GitHubService.java</path>
<content>package com.hackathon.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
public class GitHubService {

    @Value("${github.token:}")
    private String githubToken;

    private static final String GITHUB_API_BASE = "https://api.github.com";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Creates a pull request with the fixed code
     *
     * @param repo         GitHub repository (format: "owner/repo")
     * @param filePath     path to the file being fixed
     * @param originalCode the buggy code
     * @param fixedCode    the fixed code
     * @param errorType    type of error being fixed
     * @return PR URL
     */
    public String createPullRequest(String repo, String filePath, String originalCode, 
                                   String fixedCode, String errorType) {
        long timestamp = System.currentTimeMillis();
        String branchName = "prod-fix-" + timestamp;

        // Handle missing GitHub token
        if (githubToken == null || githubToken.trim().isEmpty()) {
            log.warn("GitHub token not configured. Returning mock PR URL for demo purposes.");
            return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
        }

        try {
            log.info("Creating pull request for repo: {}, file: {}, error: {}", repo, filePath, errorType);

            // Get default branch and latest commit
            String defaultBranch = getDefaultBranch(repo);
            if (defaultBranch == null) {
                log.error("Failed to get default branch for repo: {}", repo);
                return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
            }

            String latestCommitSha = getLatestCommitSha(repo, defaultBranch);
            if (latestCommitSha == null) {
                log.error("Failed to get latest commit SHA for repo: {}", repo);
                return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
            }

            // Create new branch
            String branchSha = createBranch(repo, branchName, latestCommitSha);
            if (branchSha == null) {
                log.error("Failed to create branch: {}", branchName);
                return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
            }

            // Commit fixed code to new branch
            String commitMessage = String.format("Auto-fix by IBM Bob: %s", errorType);
            String commitSha = commitFile(repo, branchName, filePath, fixedCode, commitMessage);
            if (commitSha == null) {
                log.error("Failed to commit file to branch: {}", branchName);
                return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
            }

            // Create pull request
            String prTitle = String.format("Auto-fix by IBM Bob: %s", errorType);
            String prBody = buildPRBody(errorType, originalCode, fixedCode, timestamp);
            String prUrl = createPR(repo, branchName, prTitle, prBody);

            log.info("Successfully created pull request: {}", prUrl);
            return prUrl;

        } catch (Exception e) {
            log.error("Error creating pull request: {}", e.getMessage(), e);
            return String.format("https://github.com/%s/pull/mock-pr-%d", repo, timestamp);
        }
    }

    /**
     * Gets the default branch of a repository
     */
    private String getDefaultBranch(String repo) {
        try {
            String url = String.format("%s/repos/%s", GITHUB_API_BASE, repo);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to get default branch. Status: {}", response.code());
                    return null;
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                return json.getString("default_branch");
            }
        } catch (Exception e) {
            log.error("Error getting default branch: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets the latest commit SHA for a branch
     */
    private String getLatestCommitSha(String repo, String branch) {
        try {
            String url = String.format("%s/repos/%s/git/refs/heads/%s", GITHUB_API_BASE, repo, branch);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to get latest commit SHA. Status: {}", response.code());
                    return null;
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                return json.getJSONObject("object").getString("sha");
            }
        } catch (Exception e) {
            log.error("Error getting latest commit SHA: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Creates a new branch in GitHub
     *
     * @param repo       GitHub repository
     * @param branchName name of the new branch
     * @param baseSha    SHA of the commit to branch from
     * @return branch SHA or null on error
     */
    private String createBranch(String repo, String branchName, String baseSha) {
        try {
            String url = String.format("%s/repos/%s/git/refs", GITHUB_API_BASE, repo);
            
            JSONObject payload = new JSONObject();
            payload.put("ref", "refs/heads/" + branchName);
            payload.put("sha", baseSha);

            RequestBody body = RequestBody.create(payload.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to create branch. Status: {}, Body: {}", 
                             response.code(), response.body().string());
                    return null;
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                log.info("Successfully created branch: {}", branchName);
                return json.getJSONObject("object").getString("sha");
            }
        } catch (Exception e) {
            log.error("Error creating branch: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Commits a file to the specified branch
     *
     * @param repo       GitHub repository
     * @param branchName branch to commit to
     * @param filePath   path to the file
     * @param content    file content
     * @param message    commit message
     * @return commit SHA or null on error
     */
    private String commitFile(String repo, String branchName, String filePath, 
                             String content, String message) {
        try {
            String url = String.format("%s/repos/%s/contents/%s", GITHUB_API_BASE, repo, filePath);
            
            // Encode content to base64
            String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());
            
            JSONObject payload = new JSONObject();
            payload.put("message", message);
            payload.put("content", encodedContent);
            payload.put("branch", branchName);

            RequestBody body = RequestBody.create(payload.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to commit file. Status: {}, Body: {}", 
                             response.code(), response.body().string());
                    return null;
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                log.info("Successfully committed file: {} to branch: {}", filePath, branchName);
                return json.getJSONObject("commit").getString("sha");
            }
        } catch (Exception e) {
            log.error("Error committing file: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Creates a pull request
     *
     * @param repo       GitHub repository
     * @param branchName source branch
     * @param title      PR title
     * @param body       PR body
     * @return PR URL or mock URL on error
     */
    private String createPR(String repo, String branchName, String title, String body) {
        try {
            String url = String.format("%s/repos/%s/pulls", GITHUB_API_BASE, repo);
            
            // Get default branch for base
            String defaultBranch = getDefaultBranch(repo);
            if (defaultBranch == null) {
                defaultBranch = "main";
            }
            
            JSONObject payload = new JSONObject();
            payload.put("title", title);
            payload.put("body", body);
            payload.put("head", branchName);
            payload.put("base", defaultBranch);

            RequestBody requestBody = RequestBody.create(payload.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to create PR. Status: {}, Body: {}", 
                             response.code(), response.body().string());
                    return String.format("https://github.com/%s/pull/mock-pr-%d", 
                                       repo, System.currentTimeMillis());
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                String prUrl = json.getString("html_url");
                log.info("Successfully created PR: {}", prUrl);
                return prUrl;
            }
        } catch (Exception e) {
            log.error("Error creating PR: {}", e.getMessage(), e);
            return String.format("https://github.com/%s/pull/mock-pr-%d", 
                               repo, System.currentTimeMillis());
        }
    }

    /**
     * Builds the PR body with error details and code snippets
     */
    private String buildPRBody(String errorType, String originalCode, 
                              String fixedCode, long timestamp) {
        return String.format(
            "## Auto-fix by IBM Bob\n\n" +
            "**Error Type:** %s\n\n" +
            "**Timestamp:** %d\n\n" +
            "### Original Code\n" +
            "```java\n%s\n```\n\n" +
            "### Fixed Code\n" +
            "```java\n%s\n```\n\n" +
            "---\n" +
            "*This PR was automatically generated by IBM Bob Code Generator*",
            errorType, timestamp, originalCode, fixedCode
        );
    }
}
</content>
<line_count>318</line_count>
</write_to_file>

---

**User:**

[write_to_file for 'backend/src/main/java/com/hackathon/service/GitHubService.java'] Result:
<file_write_result>
<path>backend/src/main/java/com/hackathon/service/GitHubService.java</path>
<operation>created</operation>
<notice>
<i>You do not need to re-read the file, as you have seen all changes</i>
<i>Proceed with the task using these changes as the new baseline.</i>
</notice>
</file_write_result>

<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\service\GitHubService.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java,backend/src/main/java/com/hackathon/service/GitHubService.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:35:39.343Z
User time zone: Asia/Karachi, UTC+5:00

# Current Cost
$0.02

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
Successfully created GitHubService.java with complete GitHub API integration:

**Key Features Implemented:**
- `@Service` annotation with `@Slf4j` logging
- GitHub token injection via `@Value("${github.token:}")`
- OkHttp client for HTTP requests

**Main Method:**
- `createPullRequest()` - Creates PR with auto-generated fixes, handles missing token gracefully with mock URLs

**Helper Methods:**
- `getDefaultBranch()` - Retrieves repository's default branch
- `getLatestCommitSha()` - Gets latest commit SHA from branch
- `createBranch()` - Creates new branch with timestamp-based naming (prod-fix-{timestamp})
- `commitFile()` - Commits fixed code to branch with base64 encoding
- `createPR()` - Creates pull request with formatted title and body
- `buildPRBody()` - Formats PR description with error type, original/fixed code snippets, and timestamp

**Error Handling:**
- Graceful fallback to mock PR URLs when token is missing or API calls fail
- Comprehensive logging for all GitHub operations
- Proper exception handling for all HTTP requests

**GitHub API Integration:**
- Uses correct endpoints (repos, git/refs, contents, pulls)
- Proper authentication headers with Bearer token
- Accept header for GitHub API v3
- Base64 encoding for file content

The service is ready to integrate with BobCodeGenerator for automated fix deployment.
</result>
</attempt_completion>

