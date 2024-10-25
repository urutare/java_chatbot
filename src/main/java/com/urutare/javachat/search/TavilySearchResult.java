package com.urutare.javachat.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

class TavilySearchResult {

    private String title;
    private String url;
    private String content;
    private String rawContent;
    private Double score;

    public TavilySearchResult(String title, String url, String content, String rawContent, Double score) {
        this.title = title;
        this.url = url;
        this.content = content;
        this.rawContent = rawContent;
        this.score = score;
    }
   public TavilySearchResult() {
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
