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