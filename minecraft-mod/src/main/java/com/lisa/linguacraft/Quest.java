
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

    // Which villager owns this quest.
    private final VillagerRole villagerRole;

    // Quest that must be completed before this one unlocks.
    private final String prerequisiteQuestId;

    private Status status;

    // Original constructor, kept for compatibility.
    public Quest(
            String id,
            String title,
            String description,
            List<String> objectives,
            String reward,
            boolean aiGenerated
    ) {
        this(
                id,
                title,
                description,
                objectives,
                reward,
                aiGenerated,
                null,
                null
        );
    }

    // New constructor with villager ownership and prerequisite.
    public Quest(
            String id,
            String title,
            String description,
            List<String> objectives,
            String reward,
            boolean aiGenerated,
            VillagerRole villagerRole,
            String prerequisiteQuestId
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.objectives = new ArrayList<>(objectives);
        this.reward = reward;
        this.aiGenerated = aiGenerated;
        this.villagerRole = villagerRole;
        this.prerequisiteQuestId = prerequisiteQuestId;
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

    public VillagerRole getVillagerRole() {
        return villagerRole;
    }

    public String getPrerequisiteQuestId() {
        return prerequisiteQuestId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
