package com.hackathon.service;

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

// Made with Bob
