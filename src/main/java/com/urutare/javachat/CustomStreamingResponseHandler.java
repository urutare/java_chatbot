package com.urutare.javachat;

import com.urutare.javachat.customTools.GlobalAgent;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomStreamingResponseHandler implements StreamingResponseHandler<AiMessage> {
    private static final Logger LOGGER = LogManager.getLogger(CustomStreamingResponseHandler.class);

    private final SearchAction action;
    private final GlobalAgent globalAgent;
    public CustomStreamingResponseHandler(SearchAction action, GlobalAgent globalAgent) {
        this.action = action;
        this.globalAgent = globalAgent;
    }

    @Override
    public void onNext(String token) {
        action.appendAnswer(token);
    }

    @Override
    public void onComplete(Response<AiMessage> response) {
        LOGGER.info("Answer is complete for '{}', size: {}", action.getQuestion(), action.getAnswer().length());
        if (!action.getAnswer().isEmpty()) {
            action.appendAnswer(String.valueOf(globalAgent.searchWeb(action.getQuestion()).getFirst()));
        }
        action.appendAnswer("\n\nAnswer is complete for '" + action.getQuestion() + "', size: "
                + action.getAnswer().length(), true);
    }

    @Override
    public void onError(Throwable error) {
        LOGGER.error("Error while receiving answer: {}", error.getMessage());
        action.appendAnswer("\n\nSomething went wrong: " + error.getMessage(), true);
    }
}
