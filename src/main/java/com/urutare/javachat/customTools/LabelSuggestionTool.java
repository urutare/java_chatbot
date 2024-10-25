package com.urutare.javachat.customTools;

import java.util.ArrayList;
import java.util.List;

public class LabelSuggestionTool {

    public static List<String> suggestLabels(String productName) {
        List<String> labels = new ArrayList<>();
        if (productName.toLowerCase().contains("laptop")) {
            labels.add("Electronics");
            labels.add("Computers");
        }
        // Add more rules as needed
        return labels;
    }
}