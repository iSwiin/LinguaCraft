
package com.lisa.linguacraft;

import java.util.ArrayList;
import java.util.List;

public class Quest {

    public enum Status {
        AVAILABLE,
        ACTIVE,
        COMPLETED
    }

    private final String id;
    private final String title;
    private final String description;
    private final List<String> objectives;
    private final String reward;
    private final boolean aiGenerated;

    private Status status;

    public Quest(
            String id,
            String title,
            String description,
            List<String> objectives,
            String reward,
            boolean aiGenerated
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.objectives = new ArrayList<>(objectives);
        this.reward = reward;
        this.aiGenerated = aiGenerated;
        this.status = Status.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getObjectives() {
        return new ArrayList<>(objectives);
    }

    public String getReward() {
        return reward;
    }

    public boolean isAiGenerated() {
        return aiGenerated;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
