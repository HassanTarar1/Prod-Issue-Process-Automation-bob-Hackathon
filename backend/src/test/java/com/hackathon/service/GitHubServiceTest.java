package com.hackathon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GitHubService Tests")
class GitHubServiceTest {

    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        gitHubService = new GitHubService();
        // Set empty token to trigger mock PR URL generation
        ReflectionTestUtils.setField(gitHubService, "githubToken", "");
    }

    @Test
    @DisplayName("Should create PR with all required parameters")
    void shouldCreatePrWithAllParameters() {
        String repo = "owner/repo";
        String filePath = "src/main/java/Example.java";
        String originalCode = "String value = null;\nvalue.toString();";
        String fixedCode = "String value = null;\nif (value != null) {\n    value.toString();\n}";
        String errorType = "NullPointerException";
        
        String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
        
        assertNotNull(prUrl);
        assertTrue(prUrl.contains("github.com"));
        assertTrue(prUrl.contains(repo));
    }

    @Test
    @DisplayName("Should handle null repository gracefully")
    void shouldHandleNullRepository() {
        String filePath = "src/main/java/Example.java";
        String originalCode = "code";
        String fixedCode = "fixed code";
        String errorType = "NullPointerException";
        
        assertDoesNotThrow(() -> {
            String prUrl = gitHubService.createPullRequest(null, filePath, originalCode, fixedCode, errorType);
            assertNotNull(prUrl);
        });
    }

    @Test
    @DisplayName("Should handle null file path gracefully")
    void shouldHandleNullFilePath() {
        String repo = "owner/repo";
        String originalCode = "code";
        String fixedCode = "fixed code";
        String errorType = "NullPointerException";
        
        assertDoesNotThrow(() -> {
            String prUrl = gitHubService.createPullRequest(repo, null, originalCode, fixedCode, errorType);
            assertNotNull(prUrl);
        });
    }

    @Test
    @DisplayName("Should return mock URL when GitHub token not configured")
    void shouldReturnMockUrlWhenTokenNotConfigured() {
        String repo = "owner/repo";
        String filePath = "src/main/java/Example.java";
        String originalCode = "String value = null;\nvalue.toString();";
        String fixedCode = "String value = null;\nif (value != null) {\n    value.toString();\n}";
        String errorType = "NullPointerException";
        
        String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
        
        assertNotNull(prUrl);
        assertTrue(prUrl.contains("github.com"));
        assertTrue(prUrl.contains("mock-pr-"));
    }

    @Test
    @DisplayName("Should handle different error types")
    void shouldHandleDifferentErrorTypes() {
        String repo = "owner/repo";
        String filePath = "src/main/java/Example.java";
        String originalCode = "buggy code";
        String fixedCode = "fixed code";
        
        String[] errorTypes = {
            "NullPointerException",
            "IndexOutOfBoundsException",
            "ArithmeticException",
            "DateTimeParseException"
        };
        
        for (String errorType : errorTypes) {
            String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
            assertNotNull(prUrl);
            assertFalse(prUrl.isEmpty());
            assertTrue(prUrl.contains("github.com"));
        }
    }

    @Test
    @DisplayName("Should handle long code snippets")
    void shouldHandleLongCodeSnippets() {
        String repo = "owner/repo";
        String filePath = "src/main/java/Example.java";
        String errorType = "NullPointerException";
        
        StringBuilder longCode = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longCode.append("Line ").append(i).append(": String value = null;\n");
        }
        
        String prUrl = gitHubService.createPullRequest(repo, filePath, longCode.toString(), 
                                                       longCode.toString() + "// fixed", errorType);
        
        assertNotNull(prUrl);
        assertFalse(prUrl.isEmpty());
    }

    @Test
    @DisplayName("Should handle special characters in error type")
    void shouldHandleSpecialCharactersInErrorType() {
        String repo = "owner/repo";
        String filePath = "src/main/java/Example.java";
        String originalCode = "code";
        String fixedCode = "fixed code";
        String errorType = "Custom<Exception>With$pecial&Characters";
        
        String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
        
        assertNotNull(prUrl);
        assertFalse(prUrl.isEmpty());
    }

    @Test
    @DisplayName("Should create unique PR URLs for multiple calls")
    void shouldCreateUniquePrUrls() throws InterruptedException {
        String repo = "owner/repo";
        String filePath = "src/main/java/Example.java";
        String originalCode = "code";
        String fixedCode = "fixed code";
        String errorType = "NullPointerException";
        
        String prUrl1 = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
        Thread.sleep(10); // Small delay to ensure different timestamps
        String prUrl2 = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
        
        assertNotNull(prUrl1);
        assertNotNull(prUrl2);
        assertNotEquals(prUrl1, prUrl2); // Should have different timestamps
    }

    @Test
    @DisplayName("Should handle empty strings gracefully")
    void shouldHandleEmptyStrings() {
        String repo = "";
        String filePath = "";
        String originalCode = "";
        String fixedCode = "";
        String errorType = "";
        
        assertDoesNotThrow(() -> {
            String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
            assertNotNull(prUrl);
        });
    }

    @Test
    @DisplayName("Should format PR URL correctly")
    void shouldFormatPrUrlCorrectly() {
        String repo = "testowner/testrepo";
        String filePath = "src/main/java/Example.java";
        String originalCode = "code";
        String fixedCode = "fixed code";
        String errorType = "NullPointerException";
        
        String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
        
        assertNotNull(prUrl);
        assertTrue(prUrl.startsWith("https://github.com/"));
        assertTrue(prUrl.contains(repo));
        assertTrue(prUrl.contains("/pull/"));
    }

    @Test
    @DisplayName("Should handle IndexOutOfBoundsException error type")
    void shouldHandleIndexOutOfBoundsException() {
        String repo = "owner/repo";
        String filePath = "src/main/java/FlightService.java";
        String originalCode = "return flights.get(index);";
        String fixedCode = "if (index >= 0 && index < flights.size()) {\n    return flights.get(index);\n}";
        String errorType = "IndexOutOfBoundsException";
        
        String prUrl = gitHubService.createPullRequest(repo, filePath, originalCode, fixedCode, errorType);
        
        assertNotNull(prUrl);
        assertTrue(prUrl.contains("github.com"));
    }
}

// Made with Bob
