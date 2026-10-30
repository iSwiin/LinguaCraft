package com.lisa.linguacraft;

public enum VillagerRole {

    BAKER(
            "Baker",
            "Bakery",
            "Food, ingredients, and ordering"
    ),

    CAFE_OWNER(
            "Cafe Owner",
            "Cafe",
            "Greetings, drinks, and everyday conversation"
    ),

    FARMER(
            "Farmer",
            "Farm",
            "Farming, vegetables, nature, and quantities"
    );

    private final String displayName;
    private final String buildingName;
    private final String learningTopic;

    VillagerRole(
            String displayName,
            String buildingName,
            String learningTopic
    ) {
        this.displayName = displayName;
        this.buildingName = buildingName;
        this.learningTopic = learningTopic;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getLearningTopic() {
        return learningTopic;
    }
}
