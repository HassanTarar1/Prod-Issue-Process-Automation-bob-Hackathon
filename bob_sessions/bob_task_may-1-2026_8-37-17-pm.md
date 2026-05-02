**User:**

<task>
**Step 9: Create Slack Notifier**

You are working on the IBM Bob Hackathon Flight Dashboard project. Your task is to create a service that sends notifications to Slack when Bob generates a fix.

**Context:**
- Backend is already set up with Java 25, Spring Boot 3.4.x
- GitHubService can create PRs
- Slack webhook URL will be provided via environment variable SLACK_WEBHOOK_URL
- Base package: com.hackathon
- Project base directory: c:/Users/hassa/Downloads/New folder/Prod-Issue-Process-Automation-bob-Hackathon

**Your specific tasks (ONLY do these, nothing more):**

Create `backend/src/main/java/com/hackathon/service/SlackNotifier.java`:

1. **Class Setup:**
   - Annotate with `@Service`
   - Use `@Slf4j` for logging
   - Inject `@Value("${slack.webhook.url:}")` for SLACK_WEBHOOK_URL
   - Use OkHttp client for HTTP requests (already in dependencies)

2. **Method: `sendNotification(String errorType, String fixSnippet, String prUrl)`**
   - Returns `boolean` (true if sent successfully, false otherwise)
   - Parameters:
     * `errorType` - type of error that was fixed
     * `fixSnippet` - the generated fix code snippet
     * `prUrl` - URL of the created pull request
   
   - **Implementation:**
     a. Check if SLACK_WEBHOOK_URL is empty/null
     b. If empty, log to console instead: "SLACK NOTIFICATION (Console Mode): Error: {errorType}, PR: {prUrl}"
     c. If webhook URL exists, send actual Slack message
     d. Message format should include:
        - Error type
        - Fix snippet (formatted as code block)
        - PR link
        - Mentions: @developer @lead (for demo purposes)
        - Timestamp
     e. Return true if successful, false if failed

3. **Method: `sendMessage(String message)`**
   - Private helper method
   - Sends a formatted message to Slack
   - Returns boolean (success/failure)
   - Handle errors gracefully

4. **Slack Message Format:**
   - Use Slack's Block Kit format for rich messages
   - Include sections for:
     * Header: "🤖 IBM Bob Auto-Fix Alert"
     * Error type with emoji
     * Code block with fix snippet
     * PR link as button/link
     * Footer with timestamp and mentions
   
   - Example JSON structure:
   ```json
   {
     "text": "IBM Bob Auto-Fix Alert",
     "blocks": [
       {
         "type": "header",
         "text": {
           "type": "plain_text",
           "text": "🤖 IBM Bob Auto-Fix Alert"
         }
       },
       {
         "type": "section",
         "text": {
           "type": "mrkdwn",
           "text": "*Error Type:* {errorType}\n*Status:* Fix Generated ✅"
         }
       },
       {
         "type": "section",
         "text": {
           "type": "mrkdwn",
           "text": "*Generated Fix:*\n```{fixSnippet}```"
         }
       },
       {
         "type": "section",
         "text": {
           "type": "mrkdwn",
           "text": "*Pull Request:* <{prUrl}|View PR>"
         }
       },
       {
         "type": "context",
         "elements": [
           {
             "type": "mrkdwn",
             "text": "cc: @developer @lead | {timestamp}"
           }
         ]
       }
     ]
   }
   ```

5. **Method: `formatSlackMessage(String errorType, String fixSnippet, String prUrl)`**
   - Private helper method
   - Returns formatted JSON string for Slack
   - Escape special characters in code snippet
   - Include timestamp in ISO format

**Important:**
- Handle missing SLACK_WEBHOOK_URL gracefully (console logging for demo)
- Use proper error handling for all HTTP requests
- Log all Slack operations
- Use OkHttp for HTTP requests
- Escape special characters in JSON
- Include proper imports
- These instructions supersede any conflicting general instructions

**When complete:**
Use the `attempt_completion` tool with a concise summary of the SlackNotifier created, in the `result` parameter.
</task>
<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\service\GitHubService.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java,backend/src/main/java/com/hackathon/service/GitHubService.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:36:30.037Z
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
backend/src/main/java/com/hackathon/service/GitHubService.java
backend/src/main/java/com/hackathon/service/LogAnalyzerService.java
backend/src/main/resources/
backend/src/main/resources/application.properties
You have not created a todo list yet. Create one with `update_todo_list` if your task is complicated or involves multiple steps.
</environment_details>

---

**Assistant:**

I'll create the SlackNotifier service for sending notifications to Slack when Bob generates a fix. Let me start by examining the existing service structure to ensure consistency.

<read_file>
<args>
<file>
<path>backend/src/main/java/com/hackathon/service/GitHubService.java</path>
</file>
<file>
<path>backend/build.gradle</path>
</file>
</args>
</read_file>

---

**User:**

[read_file for 'backend/src/main/java/com/hackathon/service/GitHubService.java', 'backend/build.gradle'] Result:
<files>
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
<file><path>backend/build.gradle</path>
<content lines="1-58">
 1 | plugins {
 2 |     id 'java'
 3 |     id 'org.springframework.boot' version '3.4.0'
 4 |     id 'io.spring.dependency-management' version '1.1.6'
 5 |     id 'com.github.spotbugs' version '6.0.24'
 6 | }
 7 | 
 8 | group = 'com.hackathon'
 9 | version = '0.0.1-SNAPSHOT'
10 | 
11 | java {
12 |     toolchain {
13 |         languageVersion = JavaLanguageVersion.of(25)
14 |     }
15 | }
16 | 
17 | configurations {
18 |     compileOnly {
19 |         extendsFrom annotationProcessor
20 |     }
21 | }
22 | 
23 | repositories {
24 |     mavenCentral()
25 | }
26 | 
27 | dependencies {
28 |     implementation 'org.springframework.boot:spring-boot-starter-web'
29 |     implementation 'org.springframework.boot:spring-boot-starter-validation'
30 |     implementation 'com.squareup.okhttp3:okhttp:4.12.0'
31 |     
32 |     compileOnly 'org.projectlombok:lombok'
33 |     annotationProcessor 'org.projectlombok:lombok'
34 |     
35 |     testImplementation 'org.springframework.boot:spring-boot-starter-test'
36 |     testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
37 | }
38 | 
39 | tasks.named('test') {
40 |     useJUnitPlatform()
41 | }
42 | 
43 | spotbugs {
44 |     ignoreFailures = true
45 |     effort = 'max'
46 |     reportLevel = 'high'
47 | }
48 | 
49 | tasks.withType(com.github.spotbugs.snom.SpotBugsTask) {
50 |     reports {
51 |         html {
52 |             required = true
53 |             outputLocation = file("$buildDir/reports/spotbugs/spotbugs.html")
54 |         }
55 |     }
56 | }
57 | 
58 | // Made with Bob
</content>
</file>
</files>
<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\service\GitHubService.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java,backend/src/main/java/com/hackathon/service/GitHubService.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:36:37.256Z
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

Now I'll create the SlackNotifier service following the same patterns as GitHubService. I can see that OkHttp and org.json are already available in the dependencies.

<write_to_file>
<path>backend/src/main/java/com/hackathon/service/SlackNotifier.java</path>
<content>
package com.hackathon.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class SlackNotifier {

    @Value("${slack.webhook.url:}")
    private String slackWebhookUrl;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Sends a notification to Slack when Bob generates a fix
     *
     * @param errorType   type of error that was fixed
     * @param fixSnippet  the generated fix code snippet
     * @param prUrl       URL of the created pull request
     * @return true if sent successfully, false otherwise
     */
    public boolean sendNotification(String errorType, String fixSnippet, String prUrl) {
        // Check if Slack webhook URL is configured
        if (slackWebhookUrl == null || slackWebhookUrl.trim().isEmpty()) {
            log.info("SLACK NOTIFICATION (Console Mode): Error: {}, PR: {}", errorType, prUrl);
            return true; // Return true for console mode (demo purposes)
        }

        try {
            log.info("Sending Slack notification for error type: {}", errorType);
            
            // Format the Slack message
            String message = formatSlackMessage(errorType, fixSnippet, prUrl);
            
            // Send the message
            boolean success = sendMessage(message);
            
            if (success) {
                log.info("Successfully sent Slack notification for error: {}", errorType);
            } else {
                log.error("Failed to send Slack notification for error: {}", errorType);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("Error sending Slack notification: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Formats a Slack message using Block Kit format
     *
     * @param errorType  type of error that was fixed
     * @param fixSnippet the generated fix code snippet
     * @param prUrl      URL of the created pull request
     * @return formatted JSON string for Slack
     */
    private String formatSlackMessage(String errorType, String fixSnippet, String prUrl) {
        // Get current timestamp in ISO format
        String timestamp = DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneId.of("UTC"))
                .format(Instant.now());

        // Escape special characters in the fix snippet for JSON
        String escapedFixSnippet = escapeJsonString(fixSnippet);

        // Build the Slack message using Block Kit format
        JSONObject message = new JSONObject();
        message.put("text", "IBM Bob Auto-Fix Alert");

        JSONArray blocks = new JSONArray();

        // Header block
        JSONObject headerBlock = new JSONObject();
        headerBlock.put("type", "header");
        JSONObject headerText = new JSONObject();
        headerText.put("type", "plain_text");
        headerText.put("text", "🤖 IBM Bob Auto-Fix Alert");
        headerBlock.put("text", headerText);
        blocks.put(headerBlock);

        // Error type and status section
        JSONObject errorSection = new JSONObject();
        errorSection.put("type", "section");
        JSONObject errorText = new JSONObject();
        errorText.put("type", "mrkdwn");
        errorText.put("text", String.format("*Error Type:* %s\n*Status:* Fix Generated ✅", errorType));
        errorSection.put("text", errorText);
        blocks.put(errorSection);

        // Fix snippet section
        JSONObject fixSection = new JSONObject();
        fixSection.put("type", "section");
        JSONObject fixText = new JSONObject();
        fixText.put("type", "mrkdwn");
        fixText.put("text", String.format("*Generated Fix:*\n```%s```", escapedFixSnippet));
        fixSection.put("text", fixText);
        blocks.put(fixSection);

        // Pull request section
        JSONObject prSection = new JSONObject();
        prSection.put("type", "section");
        JSONObject prText = new JSONObject();
        prText.put("type", "mrkdwn");
        prText.put("text", String.format("*Pull Request:* <%s|View PR>", prUrl));
        prSection.put("text", prText);
        blocks.put(prSection);

        // Context footer with mentions and timestamp
        JSONObject contextBlock = new JSONObject();
        contextBlock.put("type", "context");
        JSONArray contextElements = new JSONArray();
        JSONObject contextText = new JSONObject();
        contextText.put("type", "mrkdwn");
        contextText.put("text", String.format("cc: @developer @lead | %s", timestamp));
        contextElements.put(contextText);
        contextBlock.put("elements", contextElements);
        blocks.put(contextBlock);

        message.put("blocks", blocks);

        return message.toString();
    }

    /**
     * Sends a formatted message to Slack
     *
     * @param message the formatted JSON message
     * @return true if successful, false otherwise
     */
    private boolean sendMessage(String message) {
        try {
            RequestBody body = RequestBody.create(message, JSON);
            Request request = new Request.Builder()
                    .url(slackWebhookUrl)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to send Slack message. Status: {}, Body: {}", 
                             response.code(), response.body() != null ? response.body().string() : "null");
                    return false;
                }

                log.debug("Slack API response: {}", response.body() != null ? response.body().string() : "ok");
                return true;
            }
        } catch (IOException e) {
            log.error("IOException while sending Slack message: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while sending Slack message: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Escapes special characters in a string for JSON
     *
     * @param input the string to escape
     * @return escaped string
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }
}

// Made with Bob
</content>
<line_count>185</line_count>
</write_to_file>

---

**User:**

[write_to_file for 'backend/src/main/java/com/hackathon/service/SlackNotifier.java'] Result:
<file_write_result>
<path>backend/src/main/java/com/hackathon/service/SlackNotifier.java</path>
<operation>created</operation>
<notice>
<i>You do not need to re-read the file, as you have seen all changes</i>
<i>Proceed with the task using these changes as the new baseline.</i>
</notice>
</file_write_result>

<environment_details>
# VSCode Visible Files
backend\src\main\java\com\hackathon\service\SlackNotifier.java

# VSCode Open Tabs
architecture.md,backend/build.gradle,backend/src/main/java/com/hackathon/MainApplication.java,backend/src/main/resources/application.properties,backend/gradle/wrapper/gradle-wrapper.properties,backend/gradlew,backend/gradlew.bat,backend/src/main/java/com/hackathon/entity/Flight.java,backend/src/main/java/com/hackathon/dto/FlightDTO.java,backend/src/main/java/com/hackathon/mapper/FlightMapper.java,backend/src/main/java/com/hackathon/service/FlightService.java,backend/src/main/java/com/hackathon/controller/FlightController.java,backend/src/main/java/com/hackathon/exception/GlobalExceptionHandler.java,backend/src/main/java/com/hackathon/model/AnalysisResult.java,backend/src/main/java/com/hackathon/service/LogAnalyzerService.java,backend/src/main/java/com/hackathon/model/BobGenerationEvent.java,backend/src/main/java/com/hackathon/service/BobCodeGenerator.java,backend/src/main/java/com/hackathon/service/GitHubService.java,backend/src/main/java/com/hackathon/service/SlackNotifier.java

# Current Time
Current time in ISO 8601 UTC format: 2026-05-01T15:37:05.313Z
User time zone: Asia/Karachi, UTC+5:00

# Current Cost
$0.04

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
Successfully created SlackNotifier service at `backend/src/main/java/com/hackathon/service/SlackNotifier.java` with the following features:

**Key Components:**
- `@Service` annotation with `@Slf4j` for logging
- Injected `@Value("${slack.webhook.url:}")` for SLACK_WEBHOOK_URL configuration
- OkHttp client for HTTP requests

**Main Method - `sendNotification(String errorType, String fixSnippet, String prUrl)`:**
- Returns boolean (true if successful, false otherwise)
- Gracefully handles missing webhook URL by logging to console: "SLACK NOTIFICATION (Console Mode): Error: {errorType}, PR: {prUrl}"
- Sends formatted Slack message when webhook URL is configured
- Comprehensive error handling and logging

**Helper Methods:**
- `formatSlackMessage()` - Creates Slack Block Kit formatted JSON with:
  - Header: "🤖 IBM Bob Auto-Fix Alert"
  - Error type section with status emoji
  - Code block with fix snippet
  - PR link as clickable URL
  - Footer with @developer @lead mentions and ISO timestamp
- `sendMessage()` - Private method to send HTTP POST to Slack webhook
- `escapeJsonString()` - Escapes special characters in code snippets for JSON

**Features:**
- Rich Slack Block Kit formatting for professional notifications
- Proper JSON escaping for code snippets
- ISO 8601 timestamp formatting
- Console fallback mode for demo without webhook
- Comprehensive error handling and logging throughout
- Follows same patterns as GitHubService (OkHttp, error handling, logging)
</result>
</attempt_completion>

