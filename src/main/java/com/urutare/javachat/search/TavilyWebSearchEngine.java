package com.urutare.javachat.search;

import dev.langchain4j.web.search.*;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static dev.langchain4j.internal.Utils.copyIfNotNull;
import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toList;

public class TavilyWebSearchEngine implements WebSearchEngine {

    private static final String DEFAULT_BASE_URL = "https://api.tavily.com/";

    private final String apiKey;
    private final TavilyClient tavilyClient;
    private final String searchDepth;
    private final Boolean includeAnswer;
    private final Boolean includeRawContent;
    private final List<String> includeDomains;
    private final List<String> excludeDomains;

    public TavilyWebSearchEngine(String baseUrl,
                                 String apiKey,
                                 Duration timeout,
                                 String searchDepth,
                                 Boolean includeAnswer,
                                 Boolean includeRawContent,
                                 List<String> includeDomains,
                                 List<String> excludeDomains) {
        this.tavilyClient = new TavilyClient(
            getOrDefault(baseUrl, DEFAULT_BASE_URL),
            getOrDefault(timeout, ofSeconds(10))
        );
        this.apiKey = ensureNotBlank(apiKey, "apiKey");
        this.searchDepth = searchDepth;
        this.includeAnswer = includeAnswer;
        this.includeRawContent = includeRawContent;
        this.includeDomains = copyIfNotNull(includeDomains);
        this.excludeDomains = copyIfNotNull(excludeDomains);
    }

    @Override
    public WebSearchResults search(WebSearchRequest webSearchRequest) {
        TavilySearchRequest request = new TavilySearchRequest(
                apiKey,
                webSearchRequest.searchTerms(),
                searchDepth,
                includeAnswer,
                includeRawContent,
                webSearchRequest.maxResults(),
                includeDomains,
                excludeDomains
        );

        TavilyResponse tavilyResponse = tavilyClient.search(request);

        final List<WebSearchOrganicResult> results = tavilyResponse.getResults().stream()
                .map(TavilyWebSearchEngine::toWebSearchOrganicResult)
                .collect(toList());

        if (tavilyResponse.getAnswer() != null) {
            WebSearchOrganicResult answerResult = WebSearchOrganicResult.from(
                    "Tavily Search API",
                    URI.create("https://tavily.com/"),
                    tavilyResponse.getAnswer(),
                    null
            );
            results.add(0, answerResult);
        }

        return WebSearchResults.from(WebSearchInformationResult.from((long) results.size()), results);
    }

    public static TavilyWebSearchEngine withApiKey(String apiKey) {
        return new TavilyWebSearchEngine(DEFAULT_BASE_URL, apiKey, ofSeconds(10), null, null, null, null, null);
    }

    private static WebSearchOrganicResult toWebSearchOrganicResult(TavilySearchResult tavilySearchResult) {
        return WebSearchOrganicResult.from(tavilySearchResult.getTitle(),
                URI.create(tavilySearchResult.getUrl().replaceAll(" ", "%20")),
                tavilySearchResult.getContent(),
                tavilySearchResult.getRawContent(),
                Collections.singletonMap("score", String.valueOf(tavilySearchResult.getScore())));
    }
}