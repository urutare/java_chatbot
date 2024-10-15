package com.urutare.javachat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiKeys {

    public static final String OPENAI_API_KEY;

    static {
        Properties properties = new Properties();
        try (InputStream input = ApiKeys.class.getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load API key from config.properties", ex);
        }
        OPENAI_API_KEY = properties.getProperty("OPENAI_API_KEY");
    }
}
