
package com.lisa.linguacraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestManager {

    private static final List<Quest> quests = new ArrayList<>();

    static {
        quests.add(new Quest(
                "greet_villager",
                "A Friendly Greeting",
                "Learn to greet someone in Japanese.",
                Arrays.asList(
                        "Learn the phrase こんにちは (Konnichiwa)",
                        "Greet a villager in Japanese"
                ),
                "Japanese basics",
                false
        ));

        quests.add(new Quest(
                "visit_shop",
                "Welcome to the Village Shop",
                "Visit a village shop and speak to the shopkeeper.",
                Arrays.asList(
                        "Find a village shop",
                        "Talk to the shopkeeper"
                ),
                "Shopping vocabulary",
                false
        ));

        quests.add(new Quest(
                "order_apple",
                "One Apple, Please!",
                "Practice ordering an item in Japanese.",
                Arrays.asList(
                        "Say りんごをください (Ringo o kudasai)",
                        "Ask the shopkeeper for an apple"
                ),
                "Food vocabulary",
                false
        ));
    }

    public static List<Quest> getAllQuests() {
        return Collections.unmodifiableList(quests);
    }

    public static List<Quest> getAvailableQuests() {
        List<Quest> result = new ArrayList<>();

        for (Quest quest : quests) {
            if (quest.getStatus() == Quest.Status.AVAILABLE) {
                result.add(quest);
            }
        }

        return result;
    }

    public static List<Quest> getActiveQuests() {
        List<Quest> result = new ArrayList<>();

        for (Quest quest : quests) {
            if (quest.getStatus() == Quest.Status.ACTIVE) {
                result.add(quest);
            }
        }

        return result;
    }

    public static List<Quest> getCompletedQuests() {
        List<Quest> result = new ArrayList<>();

        for (Quest quest : quests) {
            if (quest.getStatus() == Quest.Status.COMPLETED) {
                result.add(quest);
            }
        }

        return result;
    }

    public static boolean acceptQuest(String questId) {
        for (Quest quest : quests) {
            if (quest.getId().equals(questId)
                    && quest.getStatus() == Quest.Status.AVAILABLE) {

                quest.setStatus(Quest.Status.ACTIVE);
                return true;
            }
        }

        return false;
    }

    public static boolean completeQuest(String questId) {
        for (Quest quest : quests) {
            if (quest.getId().equals(questId)
                    && quest.getStatus() == Quest.Status.ACTIVE) {

                quest.setStatus(Quest.Status.COMPLETED);
                return true;
            }
        }

        return false;
    }

    public static void addQuest(Quest quest) {
        for (Quest existing : quests) {
            if (existing.getId().equals(quest.getId())) {
                return;
            }
        }

        quests.add(quest);
    }
}
