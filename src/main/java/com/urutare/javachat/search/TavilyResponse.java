package com.urutare.javachat.search;

import java.util.List;

class TavilyResponse {

    private String answer;
    private String query;
    private Double responseTime;
    private List<String> images;
    private List<String> followUpQuestions;
    private List<TavilySearchResult> results;

    public TavilyResponse() {
    }

    public TavilyResponse(String answer, String query, Double responseTime, List<String> images, List<String> followUpQuestions, List<TavilySearchResult> results) {
        this.answer = answer;
        this.query = query;
        this.responseTime = responseTime;
        this.images = images;
        this.followUpQuestions = followUpQuestions;
        this.results = results;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Double responseTime) {
        this.responseTime = responseTime;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getFollowUpQuestions() {
        return followUpQuestions;
    }

    public void setFollowUpQuestions(List<String> followUpQuestions) {
        this.followUpQuestions = followUpQuestions;
    }

    public List<TavilySearchResult> getResults() {
        return results;
    }

    public void setResults(List<TavilySearchResult> results) {
        this.results = results;
    }
}