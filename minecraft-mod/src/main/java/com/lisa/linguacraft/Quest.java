
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
    private final List<Boolean> completedObjectives;
    private final String reward;
    private final boolean aiGenerated;
    private final VillagerRole villagerRole;
    private final String prerequisiteQuestId;

    private Status status;

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
        this.completedObjectives = new ArrayList<>();

        for (int i = 0; i < objectives.size(); i++) {
            completedObjectives.add(false);
        }

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

    public boolean isObjectiveCompleted(int index) {
        if (index < 0 || index >= completedObjectives.size()) {
            return false;
        }

        return completedObjectives.get(index);
    }

    public boolean completeObjective(int index) {

        if (index < 0 || index >= completedObjectives.size()) {
            return false;
        }

        if (completedObjectives.get(index)) {
            return false;
        }

        completedObjectives.set(index, true);
        return true;
    }

    public int getCompletedObjectiveCount() {

        int count = 0;

        for (boolean completed : completedObjectives) {
            if (completed) {
                count++;
            }
        }

        return count;
    }

    public int getTotalObjectiveCount() {
        return objectives.size();
    }

    public boolean areAllObjectivesCompleted() {

        return !completedObjectives.isEmpty()
                && completedObjectives.stream().allMatch(Boolean::booleanValue);
    }
}
