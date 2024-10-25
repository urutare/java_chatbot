package com.urutare.javachat.customTools;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class TranslationTool {
    private final Translate translate;
    public TranslationTool() {
        translate = TranslateOptions.getDefaultInstance().getService();
    }
    public String translateToEnglish(String text, String sourceLanguage) {
        try{
            if(sourceLanguage.equals("en")) {
                return text;
            }
            Translation translation = translate.translate(
                    text,
                    Translate.TranslateOption.sourceLanguage(sourceLanguage),
                    Translate.TranslateOption.targetLanguage("en")
            );
            return translation.getTranslatedText();
        }
        catch (Exception e) {
            System.err.println("Error translating text: " + text);
            return text;
        }
    }
    public String detectLanguage(String text) {
        Detection detection = translate.detect(text);
        return detection.getLanguage();
    }
}