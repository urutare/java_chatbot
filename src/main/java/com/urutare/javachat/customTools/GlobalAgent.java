package com.urutare.javachat.customTools;

import com.urutare.javachat.ApiKeys;
import dev.langchain4j.web.search.WebSearchOrganicResult;

import java.util.List;

public class GlobalAgent {
    private final SearchTool searchTool;
    private final TranslationTool translationTool;
//    private final OpenAiStreamingChatModel chatModel;

    public GlobalAgent() {
        this.translationTool = new TranslationTool(ApiKeys.GOOGLE_API_KEY);
        this.searchTool = new SearchTool();
    }

    public List<WebSearchOrganicResult> searchWeb(String query) {
        return searchTool.searchWithAnswer(query);
    }


    public String detectLanguage(String text) {
        return translationTool.detectLanguage(text);
    }

    public String translateToEnglish(String text, String sourceLanguage) {
        return translationTool.translateToEnglish(text, sourceLanguage);
    }
}