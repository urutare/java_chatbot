package com.urutare.javachat.customTools;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.Translate.TranslateOption;

public class TranslationTool {
    private final Translate translate;

    public TranslationTool(String apiKey) {
        this.translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();

    }

    public String detectLanguage(String text) {
        return translate.detect(text).getLanguage();
    }

    public String translateToEnglish(String text, String sourceLanguage) {
        if ("en".equals(sourceLanguage)) {
            return text;
        }
        Translation translation = translate.translate(
                text,
                TranslateOption.sourceLanguage(sourceLanguage),
                TranslateOption.targetLanguage("en")
        );
        return translation.getTranslatedText();
    }
}