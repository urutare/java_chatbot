package com.urutare.javachat.search;

import java.util.List;

class TavilySearchRequest {

    private String apiKey;
    private String query;
    private String searchDepth;
    private Boolean includeAnswer;
    private Boolean includeRawContent;
    private Integer maxResults;
    private List<String> includeDomains;
    private List<String> excludeDomains;

    public TavilySearchRequest(String apiKey, String query, String searchDepth, Boolean includeAnswer, Boolean includeRawContent, Integer maxResults, List<String> includeDomains, List<String> excludeDomains) {
        this.apiKey = apiKey;
        this.query = query;
        this.searchDepth = searchDepth;
        this.includeAnswer = includeAnswer;
        this.includeRawContent = includeRawContent;
        this.maxResults = maxResults;
        this.includeDomains = includeDomains;
        this.excludeDomains = excludeDomains;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getQuery() {
        return query;
    }

    public String getSearchDepth() {
        return searchDepth;
    }

    public Boolean getIncludeAnswer() {
        return includeAnswer;
    }

    public Boolean getIncludeRawContent() {
        return includeRawContent;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public List<String> getIncludeDomains() {
        return includeDomains;
    }

    public List<String> getExcludeDomains() {
        return excludeDomains;
    }
}