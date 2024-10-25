package com.urutare.javachat;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Content section on a documentation page. Each page gets split into sections to be able to link directly to anchors.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContentSection(
        @JsonProperty("_id") IdWrapper idWrapper,
        @JsonProperty("name") String name,
        @JsonProperty("data") DataWrapper dataWrapper,
        @JsonProperty("protectedData") boolean protectedData,
        @JsonProperty("id") String id,
        @JsonProperty("date") String date,
        @JsonProperty("actorId") String actorId,
        @JsonProperty("error") String error,
        @JsonProperty("status") String status,
        @JsonProperty("businessId") String businessId,
        @JsonProperty("branchId") String branchId,
        @JsonProperty("deviceId") String deviceId,
        @JsonDeserialize(using = CustomDateTimeDeserializer.class)
        @JsonProperty("createdAt") String createdAt,
        @JsonDeserialize(using = CustomDateTimeDeserializer.class)
        @JsonProperty("updatedAt") String updatedAt,
        @JsonProperty("actionId") String actionId) {

    public record IdWrapper(@JsonProperty("$oid") String oid) {}

    public static class DataWrapper {
        private final Map<String, Object> data;

        @JsonCreator
        public DataWrapper() {
            this.data = new HashMap<>();
        }

        @JsonAnySetter
        public void setData(String key, Object value) {
            data.put(key, value);
        }

        public Map<String, Object> getData() {
            return data;
        }
    }
}