package com.urutare.javachat;

import com.urutare.javachat.customTools.GlobalAgent;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.web.search.WebSearchOrganicResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class CustomStreamingResponseHandler implements StreamingResponseHandler<AiMessage> {
    private static final Logger LOGGER = LogManager.getLogger(CustomStreamingResponseHandler.class);

    private final SearchAction action;
    private final GlobalAgent globalAgent;
    private final StringBuilder responseBuilder = new StringBuilder();
    private final AtomicBoolean isCompleted = new AtomicBoolean(false);
    private final ReentrantLock lock = new ReentrantLock();

    public CustomStreamingResponseHandler(SearchAction action, GlobalAgent globalAgent) {
        this.action = action;
        this.globalAgent = globalAgent;
    }

    @Override
    public void onNext(String token) {
        lock.lock();
        try {
            responseBuilder.append(token);
//            action.appendAnswer(token, false);
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

            if (completeAnswer.contains("[TRANSLATE]")) {
                String productName = extractContentAfterTag(completeAnswer, "[TRANSLATE]");
                String detectedLanguage = globalAgent.detectLanguage(productName);
                String translatedName = globalAgent.translateToEnglish(productName, detectedLanguage);
                action.appendAnswer("\nTranslated product name: " + translatedName + "\n", false);
            }
            if (completeAnswer.contains("[PRODUCT NAME]")) {
                String productName = extractContentAfterTag(completeAnswer, "[PRODUCT NAME]");
                System.out.println("Product name: " + productName);
                String detectedLanguage = globalAgent.detectLanguage(productName);
                String translatedName = globalAgent.translateToEnglish(productName, detectedLanguage);
                action.appendAnswer("\nTranslated product name: " + translatedName + "\n", false);

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

            action.appendAnswer( completeAnswer, true);
        } finally {
            lock.unlock();
        }
    }

    private String extractContentAfterTag(String text, String tag) {
        int startIndex = text.indexOf(tag) + tag.length();
        int endIndex = text.indexOf("\n", startIndex);
        return (endIndex == -1) ? text.substring(startIndex).trim() : text.substring(startIndex, endIndex).trim();
    }

    @Override
    public void onError(Throwable error) {
        LOGGER.error("Error while receiving answer: {}", error.getMessage(), error);
        action.appendAnswer("\n\nAn error occurred: " + error.getMessage(), true);
    }
}