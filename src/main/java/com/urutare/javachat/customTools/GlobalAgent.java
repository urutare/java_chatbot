package com.urutare.javachat.customTools;

import dev.langchain4j.web.search.WebSearchOrganicResult;

import java.util.List;

public class GlobalAgent {
    SearchTool searchTool;
    public GlobalAgent() {
        TranslationTool translationTool = new TranslationTool();
        LabelSuggestionTool labelSuggestionTool = new LabelSuggestionTool();
        searchTool = new SearchTool();
    }

    public List<WebSearchOrganicResult> searchWeb(String query) {
        return searchTool.searchWithAnswer(query);
    }

}
