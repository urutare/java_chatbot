package com.urutare.javachat;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActionService {
    private List<Action> actions;

    public void loadActions(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        actions = objectMapper.readValue(new File(filePath), objectMapper.getTypeFactory().constructCollectionType(List.class, Action.class));
    }

    public void pollUpdates(String filePath, long interval) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    loadActions(filePath);
                    // Process new actions here
                } catch (IOException e) {
                    System.Logger logger = System.getLogger(ActionService.class.getName());
                    logger.log(System.Logger.Level.ERROR, "Failed to load actions from file", e);
                }
            }
        }, 0, interval);
    }
}