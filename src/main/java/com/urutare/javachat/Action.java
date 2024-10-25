package com.urutare.javachat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Action {
    private String name;
    private CreatedAt createdAt;

    // Getters and setters

    public static class CreatedAt {
        @JsonProperty("$date")
        private String date;

        // Getters and setters
    }
}