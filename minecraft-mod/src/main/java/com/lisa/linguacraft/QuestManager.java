
package com.lisa.linguacraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestManager {

    private static final List<Quest> quests = new ArrayList<>();

    static {

        // BAKER BRANCH

        quests.add(new Quest(
                "baker_greeting",
                "Basic Communication",
                "Learn how to greet the village baker.",
                Arrays.asList(
                        "Learn こんにちは (Konnichiwa)",
                        "Greet the baker in Japanese"
                ),
                "Japanese basics",
                false,
                VillagerRole.BAKER,
                null
        ));

        quests.add(new Quest(
                "baker_food",
                "Food Vocabulary",
                "Discover the Japanese names for common foods.",
                Arrays.asList(
                        "Learn パン (Pan) - Bread",
                        "Learn ケーキ (Keeki) - Cake"
                ),
                "Food vocabulary",
                false,
                VillagerRole.BAKER,
                "baker_greeting"
        ));

        quests.add(new Quest(
                "baker_order",
                "Ordering Food",
                "Learn how to politely order food.",
                Arrays.asList(
                        "Learn パンをください (Pan o kudasai)",
                        "Order bread from the baker"
                ),
                "Ordering vocabulary",
                false,
                VillagerRole.BAKER,
                "baker_food"
        ));

        quests.add(new Quest(
                "baker_ingredients",
                "Ingredients",
                "Learn the names of ingredients used in baking.",
                Arrays.asList(
                        "Learn たまご (Tamago) - Egg",
                        "Learn こむぎこ (Komugiko) - Flour"
                ),
                "Ingredient vocabulary",
                false,
                VillagerRole.BAKER,
                "baker_food"
        ));

        // CAFE OWNER BRANCH

        quests.add(new Quest(
                "cafe_greeting",
                "Everyday Greetings",
                "Practice greetings with the cafe owner.",
                Arrays.asList(
                        "Learn こんにちは (Konnichiwa)",
                        "Greet the cafe owner in Japanese"
                ),
                "Conversation basics",
                false,
                VillagerRole.CAFE_OWNER,
                null
        ));

        quests.add(new Quest(
                "cafe_drinks",
                "Drinks Vocabulary",
                "Discover the names of popular drinks.",
                Arrays.asList(
                        "Learn コーヒー (Koohii) - Coffee",
                        "Learn おちゃ (Ocha) - Tea"
                ),
                "Drink vocabulary",
                false,
                VillagerRole.CAFE_OWNER,
                "cafe_greeting"
        ));

        quests.add(new Quest(
                "cafe_order",
                "Ordering Drinks",
                "Practice ordering a drink politely.",
                Arrays.asList(
                        "Learn コーヒーをください (Koohii o kudasai)",
                        "Order coffee from the cafe owner"
                ),
                "Ordering drinks",
                false,
                VillagerRole.CAFE_OWNER,
                "cafe_drinks"
        ));

        quests.add(new Quest(
                "cafe_conversation",
                "Everyday Conversation",
                "Learn expressions for everyday interactions.",
                Arrays.asList(
                        "Learn ありがとう (Arigatou) - Thank you",
                        "Thank the cafe owner in Japanese"
                ),
                "Conversation skills",
                false,
                VillagerRole.CAFE_OWNER,
                "cafe_drinks"
        ));

        // FARMER BRANCH

        quests.add(new Quest(
                "farmer_greeting",
                "Meeting the Farmer",
                "Introduce yourself to the village farmer.",
                Arrays.asList(
                        "Learn こんにちは (Konnichiwa)",
                        "Greet the farmer in Japanese"
                ),
                "Japanese basics",
                false,
                VillagerRole.FARMER,
                null
        ));

        quests.add(new Quest(
                "farmer_vegetables",
                "Vegetable Vocabulary",
                "Learn the names of vegetables.",
                Arrays.asList(
                        "Learn にんじん (Ninjin) - Carrot",
                        "Learn じゃがいも (Jagaimo) - Potato"
                ),
                "Vegetable vocabulary",
                false,
                VillagerRole.FARMER,
                "farmer_greeting"
        ));

        quests.add(new Quest(
                "farmer_numbers",
                "Numbers and Quantities",
                "Learn how to count items and quantities.",
                Arrays.asList(
                        "Learn いち (Ichi) - One",
                        "Learn に (Ni) - Two",
                        "Learn さん (San) - Three"
                ),
                "Numbers and quantities",
                false,
                VillagerRole.FARMER,
                "farmer_vegetables"
        ));

        quests.add(new Quest(
                "farmer_seasons",
                "Seasons and Nature",
                "Discover vocabulary about seasons and nature.",
                Arrays.asList(
                        "Learn はる (Haru) - Spring",
                        "Learn なつ (Natsu) - Summer"
                ),
                "Nature vocabulary",
                false,
                VillagerRole.FARMER,
                "farmer_vegetables"
        ));

        quests.add(new Quest(
                "farmer_trade",
                "Trading the Harvest",
                "Use your vocabulary to trade crops.",
                Arrays.asList(
                        "Learn やさい (Yasai) - Vegetables",
                        "Learn やさいをください (Yasai o kudasai)",
                        "Ask the farmer for vegetables"
                ),
                "Trading vocabulary",
                false,
                VillagerRole.FARMER,
                "farmer_numbers"
        ));

        activateStartingQuests();
    }

    private static void activateStartingQuests() {

        for (Quest quest : quests) {

            if (quest.getPrerequisiteQuestId() == null) {
                quest.setStatus(Quest.Status.ACTIVE);
            }
        }
    }

    public static List<Quest> getAllQuests() {
        return Collections.unmodifiableList(quests);
    }

    public static List<Quest> getAvailableQuests() {

        List<Quest> result = new ArrayList<>();

        for (Quest quest : quests) {

            if (quest.getStatus() == Quest.Status.AVAILABLE
                    && isPrerequisiteCompleted(quest)) {

                result.add(quest);
            }
        }

        return result;
    }

    public static List<Quest> getAvailableQuests(VillagerRole role) {

        List<Quest> result = new ArrayList<>();

        for (Quest quest : getAvailableQuests()) {

            if (quest.getVillagerRole() == role) {
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

    public static List<Quest> getQuestsForRole(VillagerRole role) {

        List<Quest> result = new ArrayList<>();

        for (Quest quest : quests) {

            if (quest.getVillagerRole() == role) {
                result.add(quest);
            }
        }

        return result;
    }

    private static boolean isPrerequisiteCompleted(Quest quest) {

        String prerequisiteId = quest.getPrerequisiteQuestId();

        if (prerequisiteId == null) {
            return true;
        }

        Quest prerequisite = findQuest(prerequisiteId);

        return prerequisite != null
                && prerequisite.getStatus() == Quest.Status.COMPLETED;
    }

    // Compatibility method. Players do not need to accept quests manually.
    public static boolean acceptQuest(String questId) {

        Quest quest = findQuest(questId);

        if (quest == null
                || quest.getStatus() != Quest.Status.AVAILABLE
                || !isPrerequisiteCompleted(quest)) {

            return false;
        }

        quest.setStatus(Quest.Status.ACTIVE);
        return true;
    }

    public static boolean completeObjective(
            String questId,
            int objectiveIndex
    ) {

        Quest quest = findQuest(questId);

        if (quest == null
                || quest.getStatus() != Quest.Status.ACTIVE) {

            return false;
        }

        boolean changed = quest.completeObjective(objectiveIndex);

        if (!changed) {
            return false;
        }

        // Complete the quest only after every objective is finished.
        if (quest.areAllObjectivesCompleted()) {
            completeQuest(questId);
        }

        return true;
    }

    public static boolean completeQuest(String questId) {

        Quest quest = findQuest(questId);

        if (quest == null
                || quest.getStatus() != Quest.Status.ACTIVE
                || !quest.areAllObjectivesCompleted()) {

            return false;
        }

        quest.setStatus(Quest.Status.COMPLETED);

        // Unlock every direct child.
        for (Quest nextQuest : quests) {

            if (questId.equals(nextQuest.getPrerequisiteQuestId())
                    && nextQuest.getStatus() == Quest.Status.AVAILABLE) {

                nextQuest.setStatus(Quest.Status.ACTIVE);
            }
        }

        return true;
    }

    public static Quest findQuest(String questId) {

        for (Quest quest : quests) {

            if (quest.getId().equals(questId)) {
                return quest;
            }
        }

        return null;
    }

    public static void addQuest(Quest quest) {

        if (findQuest(quest.getId()) != null) {
            return;
        }

        quests.add(quest);

        if (quest.getPrerequisiteQuestId() == null) {
            quest.setStatus(Quest.Status.ACTIVE);
        }
    }
}
