package com.urutare.javachat;

import com.urutare.javachat.customTools.GlobalAgent;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.web.search.WebSearchOrganicResult;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CustomStreamingResponseHandler implements StreamingResponseHandler<AiMessage> {
    private static final Logger LOGGER = LogManager.getLogger(CustomStreamingResponseHandler.class);

    private final SearchAction action;
    private final GlobalAgent globalAgent;
    private final StringBuilder responseBuilder = new StringBuilder();
    private final AtomicBoolean isCompleted = new AtomicBoolean(false);
    private final ReentrantLock lock = new ReentrantLock();

    public static final List<String> conversationHistory = new ArrayList<>();
    @Getter
    private List<String> suggestedLabels = new ArrayList<>();

    @Getter
    private static String currentContext = "";
    public CustomStreamingResponseHandler(SearchAction action, GlobalAgent globalAgent) {
        this.action = action;
        this.globalAgent = globalAgent;
    }

    @Override
    public void onNext(String token) {
        lock.lock();
        try {
            responseBuilder.append(token);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onComplete(Response<AiMessage> response) {
        if (isCompleted.getAndSet(true)) {
            return;
        }

        lock.lock();
        try {
            String completeAnswer = responseBuilder.toString().trim();
            LOGGER.info("Complete answer: {}", completeAnswer);
            System.out.println("Complete answer: " + completeAnswer);

            // Add the complete answer to the conversation history
            conversationHistory.add(completeAnswer);
            String originalLanguage = null;
            String productName = null;

            if (completeAnswer.contains("[TRANSLATE]")) {
                System.out.println("Translating product name");
                productName = extractContentAfterTag(completeAnswer, "[TRANSLATE]");
                String detectedLanguage = globalAgent.detectLanguage(productName);
                originalLanguage = detectedLanguage;
                String translatedName = globalAgent.translateToEnglish(productName, detectedLanguage);
                action.appendAnswer("\nTranslated product name: " + translatedName + "\n", false);
            }
            if (completeAnswer.contains("[PRODUCT NAME]")) {
                productName = extractContentAfterTag(completeAnswer, "[PRODUCT NAME]");
                System.out.println("Product name: " + productName);
                String detectedLanguage = globalAgent.detectLanguage(productName);
                originalLanguage = detectedLanguage;
                String translatedName = globalAgent.translateToEnglish(productName, detectedLanguage);
                action.appendAnswer("\nTranslated product name: " + translatedName + "\n", false);
            }
            if (completeAnswer.contains("[PRODUCT NAME]") || completeAnswer.contains("Suggested labels for")) {
                currentContext = "tag_suggestion";
            }
            if (completeAnswer.contains("[LABELS_START]")) {
                System.out.println("Logging suggested labels");
                suggestedLabels = extractLabels(completeAnswer);
                for (String label : suggestedLabels) {
                    System.out.println("Suggested label: " + label);
                }
                System.out.println("the detected language is: " + originalLanguage);
                if (originalLanguage != null && !originalLanguage.equals("en")) {
                    List<String> translatedLabels = new ArrayList<>();
                    for (String label : suggestedLabels) {
                        String translatedLabel = globalAgent.translateFromEnglish(label, originalLanguage);
                        translatedLabels.add(translatedLabel);
                    }
                    suggestedLabels = translatedLabels;
                    System.out.println("Translated labels: " + translatedLabels);
                } else {
                    System.out.println("No translation needed");
                }
            }

            if (completeAnswer.contains("[WEB SEARCH REQUIRED]") || completeAnswer.contains("[ADDITIONAL INFO REQUIRED]")) {
                String searchQuery = extractContentAfterTag(completeAnswer, "[WEB SEARCH REQUIRED]");
                if(searchQuery.isEmpty()) {
                    searchQuery = extractContentAfterTag(completeAnswer, "[ADDITIONAL INFO REQUIRED]");
                }
                List<WebSearchOrganicResult> searchResults = globalAgent.searchWeb(searchQuery);
                action.appendAnswer("\nWeb Search Results:\n", false);
                for (int i = 0; i < Math.min(searchResults.size(), 3); i++) {
                    WebSearchOrganicResult result = searchResults.get(i);
                    action.appendAnswer((i + 1) + ". " + result.title() + "\n", false);
                    action.appendAnswer("   " + result.url() + "\n", false);
                    action.appendAnswer("   " + result.content() + "\n\n", false);
                }
            }

            action.appendAnswer(completeAnswer, true);
        } finally {
            lock.unlock();
        }
    }

    private String extractContentAfterTag(String text, String tag) {
        int startIndex = text.indexOf(tag) + tag.length();
        int endIndex = text.indexOf("\n", startIndex);
        return (endIndex == -1) ? text.substring(startIndex).trim() : text.substring(startIndex, endIndex).trim();
    }

    public static List<String> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

   private List<String> extractLabels(String text) {
    List<String> labels = new ArrayList<>();
    int start = text.indexOf("[LABELS_START]");
    int end = text.indexOf("[LABELS_END]");

    if (start != -1 && end != -1 && start < end) {
        String labelsText = text.substring(start + "[LABELS_START]".length(), end).trim();
        labels = Arrays.stream(labelsText.split("\n"))
                .map(String::trim)
                .filter(label -> !label.matches(".*\\d.*") || label.matches("^\\d+\\.\\s+.*") || label.startsWith("- "))
                .map(label -> {
                    if (label.matches("^\\d+\\.\\s+.*")) {
                        return label.replaceFirst("^\\d+\\.\\s+", "");
                    } else if (label.startsWith("- ")) {
                        return label.substring(2).trim();
                    } else {
                        return label;
                    }
                })
                .collect(Collectors.toList());
    }
    return labels;
}


    public static void clearContext() {
        currentContext = "";
    }
    @Override
    public void onError(Throwable error) {
        LOGGER.error("Error while receiving answer: {}", error.getMessage(), error);
        action.appendAnswer("\n\nAn error occurred: " + error.getMessage(), true);
    }
}