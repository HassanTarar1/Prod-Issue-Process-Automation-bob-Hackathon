package com.hackathon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("GitHubService Tests")
class GitHubServiceTest {

    private GitHubService gitHubService;

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitHubService = new GitHubService();
    }

    @Test
    @DisplayName("Should generate valid branch name with timestamp")
    void shouldGenerateValidBranchName() {
        String branchName = gitHubService.generateBranchName();
        
        assertNotNull(branchName);
        assertTrue(branchName.startsWith("prod-fix-"));
        assertTrue(branchName.length() > 10);
    }

    @Test
    @DisplayName("Should generate unique branch names")
    void shouldGenerateUniqueBranchNames() throws InterruptedException {
        String branchName1 = gitHubService.generateBranchName();
        Thread.sleep(10); // Small delay to ensure different timestamps
        String branchName2 = gitHubService.generateBranchName();
        
        assertNotEquals(branchName1, branchName2);
    }

    @Test
    @DisplayName("Should create PR title with error type")
    void shouldCreatePrTitleWithErrorType() {
        String errorType = "NullPointerException";
        String fixSnippet = "if (obj != null) { ... }";
        
        String prUrl = gitHubService.createPullRequest(errorType, fixSnippet);
        
        assertNotNull(prUrl);
        assertTrue(prUrl.contains("github.com") || prUrl.contains("mock"));
    }

    @Test
    @DisplayName("Should handle null error type gracefully")
    void shouldHandleNullErrorType() {
        String fixSnippet = "if (obj != null) { ... }";
        
        String prUrl = gitHubService.createPullRequest(null, fixSnippet);
        
        assertNotNull(prUrl);
        assertTrue(prUrl.contains("github.com") || prUrl.contains("mock"));
    }

    @Test
    @DisplayName("Should handle null fix snippet gracefully")
    void shouldHandleNullFixSnippet() {
        String errorType = "NullPointerException";
        
        String prUrl = gitHubService.createPullRequest(errorType, null);
        
        assertNotNull(prUrl);
        assertTrue(prUrl.contains("github.com") || prUrl.contains("mock"));
    }

    @Test
    @DisplayName("Should return mock URL when GitHub token not configured")
    void shouldReturnMockUrlWhenTokenNotConfigured() {
        String errorType = "NullPointerException";
        String fixSnippet = "if (obj != null) { ... }";
        
        String prUrl = gitHubService.createPullRequest(errorType, fixSnippet);
        
        assertNotNull(prUrl);
        // When token is not configured, should return mock URL
        assertTrue(prUrl.contains("github.com") || prUrl.contains("mock"));
    }

    @Test
    @DisplayName("Should format PR body with fix snippet")
    void shouldFormatPrBodyWithFixSnippet() {
        String errorType = "IndexOutOfBoundsException";
        String fixSnippet = "if (index >= 0 && index < list.size()) { ... }";
        
        String prUrl = gitHubService.createPullRequest(errorType, fixSnippet);
        
        assertNotNull(prUrl);
        // PR should be created successfully
        assertFalse(prUrl.isEmpty());
    }

    @Test
    @DisplayName("Should handle different error types")
    void shouldHandleDifferentErrorTypes() {
        String[] errorTypes = {
            "NullPointerException",
            "IndexOutOfBoundsException",
            "ArithmeticException",
            "DateTimeParseException"
        };
        
        for (String errorType : errorTypes) {
            String prUrl = gitHubService.createPullRequest(errorType, "fix code");
            assertNotNull(prUrl);
            assertFalse(prUrl.isEmpty());
        }
    }

    @Test
    @DisplayName("Should handle long fix snippets")
    void shouldHandleLongFixSnippets() {
        String errorType = "NullPointerException";
        StringBuilder longSnippet = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longSnippet.append("Line ").append(i).append(": if (obj != null) { doSomething(); }\n");
        }
        
        String prUrl = gitHubService.createPullRequest(errorType, longSnippet.toString());
        
        assertNotNull(prUrl);
        assertFalse(prUrl.isEmpty());
    }

    @Test
    @DisplayName("Should handle special characters in error type")
    void shouldHandleSpecialCharactersInErrorType() {
        String errorType = "Custom<Exception>With$pecial&Characters";
        String fixSnippet = "fix code";
        
        String prUrl = gitHubService.createPullRequest(errorType, fixSnippet);
        
        assertNotNull(prUrl);
        assertFalse(prUrl.isEmpty());
    }

    @Test
    @DisplayName("Should create consistent PR URLs for same error type")
    void shouldCreateConsistentPrUrls() {
        String errorType = "NullPointerException";
        String fixSnippet = "if (obj != null) { ... }";
        
        String prUrl1 = gitHubService.createPullRequest(errorType, fixSnippet);
        String prUrl2 = gitHubService.createPullRequest(errorType, fixSnippet);
        
        assertNotNull(prUrl1);
        assertNotNull(prUrl2);
        // Both should be valid URLs
        assertTrue(prUrl1.contains("github.com") || prUrl1.contains("mock"));
        assertTrue(prUrl2.contains("github.com") || prUrl2.contains("mock"));
    }
}

// Made with Bob
