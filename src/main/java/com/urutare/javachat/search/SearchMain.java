package com.urutare.javachat.search;

import com.urutare.javachat.ApiKeys;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchOrganicResult;
import dev.langchain4j.web.search.WebSearchResults;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class SearchMain {
    private static String tavilyApiKey;
        public static void main(String[] args) {
            tavilyApiKey = ApiKeys.TAVILY_API_KEY;
            SearchMain searchMain = new SearchMain();
            searchMain.searchWithAnswer();
            searchMain.searchWithRawContent();

        }
    void searchWithRawContent() {
        // Given
        TavilyWebSearchEngine tavilyWebSearchEngine = new TavilyWebSearchEngine(
            "https://api.tavily.com/",
                tavilyApiKey,
            Duration.ofSeconds(50),
            "1",
            false,
            true,
            Collections.singletonList("example.com"),
            Collections.singletonList("example.org")
        );

        WebSearchResults webSearchResults = tavilyWebSearchEngine.search("What is LangChain4j?");

        // then
        List<WebSearchOrganicResult> results = webSearchResults.results();

        System.out.println(results);

    }
    void searchWithAnswer() {
        TavilyWebSearchEngine tavilyWebSearchEngine = new TavilyWebSearchEngine(
            "https://api.tavily.com/",
            tavilyApiKey,
            Duration.ofSeconds(50),
            "1",
            true,
            false,
            Collections.singletonList("example.com"),
            Collections.singletonList("example.org")
        );
        WebSearchResults webSearchResults = tavilyWebSearchEngine.search("What is Java?");


        List<WebSearchOrganicResult> results = webSearchResults.results();

        System.out.println(results);
    }

    public String getTavilyApiKey() {
        return tavilyApiKey;
    }

    public void setTavilyApiKey(String tavilyApiKey) {
        SearchMain.tavilyApiKey = tavilyApiKey;
    }
}
