package com.urutare.javachat.customTools;

import dev.langchain4j.web.search.WebSearchOrganicResult;

import java.util.List;

public class GlobalAgent {
    private final SearchTool searchTool;
    private final TranslationTool translationTool;
//    private final OpenAiStreamingChatModel chatModel;

    public GlobalAgent() {
        this.translationTool = new TranslationTool();
        this.searchTool = new SearchTool();
//        this.chatModel = OpenAiStreamingChatModel.builder()
//                .apiKey(ApiKeys.OPENAI_API_KEY)
//                .modelName("gpt-3.5-turbo")
//                .build();
    }

    public List<WebSearchOrganicResult> searchWeb(String query) {
        return searchTool.searchWithAnswer(query);
    }

//    public List<String> suggestLabels(String productName) {
//        String prompt = "Suggest 5 relevant labels or tags for the product: " + productName +
//                "\nProvide only the labels, one per line, without numbering or additional text.";
//
//        SearchAction tempAction = new SearchAction("Suggesting labels for: " + productName);
//        chatModel.generate(prompt, new CustomStreamingResponseHandler(tempAction, this));
//        // Wait for the response to be complete
//        while (!tempAction.isComplete()) {
//            synchronized (this) {
//                while (!tempAction.isComplete()) {
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        return Collections.emptyList();
//                    }
//                }
//            }
//        }
//
//        return Arrays.asList(tempAction.getAnswer().split("\n"));
//    }

    public String detectLanguage(String text) {
        return translationTool.detectLanguage(text);
    }

    public String translateToEnglish(String text, String sourceLanguage) {
        return translationTool.translateToEnglish(text, sourceLanguage);
    }
}