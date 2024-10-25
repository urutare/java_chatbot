package com.urutare.javachat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urutare.javachat.customTools.GlobalAgent;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
//import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

// Based on some of the LangChain4J examples:
// https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ChatWithDocumentsExamples.java
// https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/embedding/store/InMemoryEmbeddingStoreExample.java
public class DocsAnswerService {

    private static final Logger LOGGER = LogManager.getLogger(DocsAnswerService.class);
    private static final int MAX_TOKENS = 8192;
    private EmbeddingModel embeddingModel;
    private EmbeddingStore<TextSegment> embeddingStore;
    private OpenAiStreamingChatModel chatModel;
    private GlobalAgent globalAgent;
    public DocsAnswerService() {

    }

    public void init(SearchAction action) {
        action.appendAnswer("Initiating...");
        var contentSections = loadJson(action);
        initChat(action, contentSections);
        globalAgent = new GlobalAgent();
        System.out.println("Global agent is created");
        System.out.println(globalAgent.searchWeb("What is the capital of Rwanda?"));
    }

    private List<ContentSection> loadJson(SearchAction action) {
        try {
            URL fileUrl = DocsAnswerService.class.getResource("bms.actions.json");
            if (fileUrl == null) {
                action.appendAnswer("\nCould not find the JSON file", true);
                return new ArrayList<>();
            }
            String json = Files.readString(Paths.get(fileUrl.toURI()));
            ObjectMapper objectMapper = new ObjectMapper();
            List<ContentSection> contentSections = objectMapper.readValue(json, new TypeReference<>() {
            });
            action.appendAnswer("\nLoaded number of JSON content sections: " + contentSections.size());
            return contentSections;
        } catch (Exception e) {
            action.appendAnswer("\nError while reading JSON data: " + e.getMessage(), true);
        }
        return new ArrayList<>();
    }

    private Metadata createMetadataFromContentSection(ContentSection contentSection) {
        HashMap<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("ID", contentSection.id() != null ? contentSection.id() : "No id found");
        metadataMap.put("ACTION_ID", contentSection.actionId() != null ? contentSection.actionId() : "no action id found");
        metadataMap.put("DATA", contentSection.dataWrapper()!= null ? "" + contentSection.dataWrapper().getData() : "no data found");
        metadataMap.put("DATE", contentSection.date() != null ? contentSection.date() : "no date found");
        metadataMap.put("NAME", contentSection.name() != null ? contentSection.name() : "no name found");
        metadataMap.put("STATUS", contentSection.status() != null ? contentSection.status() : "no status found");
        return new Metadata(metadataMap);
    }

    private void initChat(SearchAction action, List<ContentSection> contentSections) {
        List<TextSegment> textSegments = new ArrayList<>();
        for (var contentSection : contentSections.stream().toList()) {
            Metadata metadata = createMetadataFromContentSection(contentSection);
//            System.out.println("Metadata: " + metadata);

            String actionId = contentSection.actionId();
            if (actionId != null && !actionId.isBlank()) {
                textSegments.add(new TextSegment(actionId, metadata));
            } else {
                LOGGER.warn("Skipping ContentSection with null or blank actionId");
            }
        }

        action.appendAnswer("\nConverted to number of text segments: " + textSegments.size());

        embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        embeddingStore = new InMemoryEmbeddingStore<>();
        action.appendAnswer("\nEmbedding store is created: " + textSegments.size());
        System.out.println("Embedding store is created: " + textSegments.size());
        System.out.println("text segments: " + textSegments);
        List<Embedding> embeddings = embeddingModel.embedAll(textSegments).content();
        action.appendAnswer("\nNumber of embeddings: " + embeddings.size());

        embeddingStore.addAll(embeddings, textSegments);
        action.appendAnswer("\nEmbeddings are added to the store");

        chatModel = OpenAiStreamingChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                // Available OpenAI models are listed on
                // https://platform.openai.com/docs/models/continuous-model-upgrades
                // gpt-4-1106-preview --> more expensive to use
                // gpt-4
                // gpt-3.5-turbo-1106
                .modelName("gpt-4")
                .build();
        action.appendAnswer("\nChat model is ready", true);
    }

    void ask(SearchAction action) {
        LOGGER.info("Asking question '{}'", action.getQuestion());

        // Find relevant embeddings in embedding store by semantic similarity
        // You can play with parameters below to find a sweet spot for your specific use case
        int maxResults = 5; // Reduced from 10
        double minScore = 0.8; // Increased from 0.7
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embeddingStore.findRelevant(embeddingModel.embed(action.getQuestion()).content(), maxResults, minScore);
        LOGGER.info("Number of relevant embeddings: {} for '{}'", relevantEmbeddings.size(), action.getQuestion());

        relevantEmbeddings.stream().map(EmbeddingMatch::embedded).toList()
                .forEach(ts -> Platform.runLater(() -> {
                    LOGGER.info("Related data: {}", ts.metadata());
                    action.appendRelatedLink("ID: " + ts.metadata("ID"));
                }));

        // Create a prompt for the model that includes question and relevant embeddings
        PromptTemplate promptTemplate = PromptTemplate.from(
                """
                Answer the following question to the best of your ability:
                    {{question}}
                
                Base your answer on these relevant actions from the logs:
                    {{information}}
                
                Follow these steps:
                1. If you can answer the question using the provided logs, do so.
                2. If you cannot find a direct answer in the logs, clearly state that you need to search for more information.
                3. When you need to search, write: "[SEARCH REQUIRED]" at the end of your response.
                
                Always provide a clear and concise answer, citing your sources (logs or web search).
                """);

        String information = relevantEmbeddings.stream()
    .map(match -> match.embedded().text()
            + ". ACTION_ID: " + match.embedded().metadata("ACTION_ID")
        + ". ID: " + match.embedded().metadata("ID")
        + ". NAME: " + match.embedded().metadata("NAME")
        + ". DATE: " + match.embedded().metadata("DATE")
            + ". DATA: " + match.embedded().metadata("DATA")
        + ". STATUS: " + match.embedded().metadata("STATUS"))
    .collect(Collectors.joining("\n\n"));
        information = truncateToFitTokenLimit(information, MAX_TOKENS-1000);
        Map<String, Object> variables = new HashMap<>();
        variables.put("question", action.getQuestion());
        variables.put("information", information);

        Prompt prompt = promptTemplate.apply(variables);

        if (chatModel != null) {
            chatModel.generate(prompt.toUserMessage().toString(), new CustomStreamingResponseHandler(action,globalAgent));
        } else {
            action.appendAnswer("The chat model is not ready yet... Please try again later.", true);
        }
    }
    private String truncateToFitTokenLimit(String text, int maxTokens) {
        // Simple token estimation: split by spaces and count
        String[] tokens = text.split("\\s+");
        if (tokens.length <= maxTokens) {
            return text;
        }
        return String.join(" ", Arrays.copyOf(tokens, maxTokens));
    }

}