
package com.lisa.linguacraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestManager {

    private static final List<Quest> quests = new ArrayList<>();

    static {

        // ==========================================
        // BAKER QUESTLINE
        // ==========================================

        quests.add(new Quest(
                "baker_greeting",
                "A Friendly Greeting",
                "Introduce yourself to the village baker.",
                Arrays.asList(
                        "Learn the phrase こんにちは (Konnichiwa)",
                        "Greet the baker in Japanese"
                ),
                "Japanese basics",
                false,
                VillagerRole.BAKER,
                null
        ));

        quests.add(new Quest(
                "baker_food",
                "The Language of Food",
                "Learn some useful Japanese food vocabulary.",
                Arrays.asList(
                        "Learn the word パン (Pan) - Bread",
                        "Learn the word ケーキ (Keeki) - Cake"
                ),
                "Food vocabulary",
                false,
                VillagerRole.BAKER,
                "baker_greeting"
        ));

        quests.add(new Quest(
                "baker_order",
                "One Bread, Please!",
                "Practice ordering something from the baker.",
                Arrays.asList(
                        "Learn パンをください (Pan o kudasai)",
                        "Ask the baker for bread in Japanese"
                ),
                "Ordering vocabulary",
                false,
                VillagerRole.BAKER,
                "baker_food"
        ));

        quests.add(new Quest(
                "baker_ingredients",
                "A Baker's Ingredients",
                "Learn the names of ingredients used in baking.",
                Arrays.asList(
                        "Learn the word たまご (Tamago) - Egg",
                        "Learn the word こむぎこ (Komugiko) - Flour"
                ),
                "Ingredient vocabulary",
                false,
                VillagerRole.BAKER,
                "baker_order"
        ));


        // ==========================================
        // CAFE OWNER QUESTLINE
        // ==========================================

        quests.add(new Quest(
                "cafe_greeting",
                "Welcome to the Cafe",
                "Introduce yourself to the cafe owner.",
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
                "Something to Drink",
                "Learn the names of popular Japanese drinks.",
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
                "I'd Like a Coffee!",
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
                "A Little Conversation",
                "Practice having a simple conversation.",
                Arrays.asList(
                        "Learn ありがとう (Arigatou) - Thank you",
                        "Thank the cafe owner in Japanese"
                ),
                "Everyday conversation",
                false,
                VillagerRole.CAFE_OWNER,
                "cafe_order"
        ));


        // ==========================================
        // FARMER QUESTLINE
        // ==========================================

        quests.add(new Quest(
                "farmer_greeting",
                "Hello, Farmer!",
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
                "What's Growing?",
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
                "Counting the Harvest",
                "Learn how to count quantities.",
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
                "farmer_trade",
                "The Harvest Trade",
                "Practice discussing and trading crops.",
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
    }

    // Return every quest.
    public static List<Quest> getAllQuests() {
        return Collections.unmodifiableList(quests);
    }

    // Return quests that are currently available.
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

    // Return available quests belonging to one villager.
    public static List<Quest> getAvailableQuests(VillagerRole role) {

        List<Quest> result = new ArrayList<>();

        for (Quest quest : getAvailableQuests()) {

            if (quest.getVillagerRole() == role) {
                result.add(quest);
            }
        }

        return result;
    }

    // Return active quests.
    public static List<Quest> getActiveQuests() {

        List<Quest> result = new ArrayList<>();

        for (Quest quest : quests) {

            if (quest.getStatus() == Quest.Status.ACTIVE) {
                result.add(quest);
            }
        }

        return result;
    }

    // Return completed quests.
    public static List<Quest> getCompletedQuests() {

        List<Quest> result = new ArrayList<>();

        for (Quest quest : quests) {

            if (quest.getStatus() == Quest.Status.COMPLETED) {
                result.add(quest);
            }
        }

        return result;
    }

    // Check whether a quest's prerequisite is completed.
    private static boolean isPrerequisiteCompleted(Quest quest) {

        String prerequisiteId = quest.getPrerequisiteQuestId();

        if (prerequisiteId == null) {
            return true;
        }

        Quest prerequisite = findQuest(prerequisiteId);

        return prerequisite != null
                && prerequisite.getStatus() == Quest.Status.COMPLETED;
    }

    // Accept an available quest.
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

    // Complete an active quest and unlock the next quest.
    public static boolean completeQuest(String questId) {

        Quest quest = findQuest(questId);

        if (quest == null
                || quest.getStatus() != Quest.Status.ACTIVE) {

            return false;
        }

        quest.setStatus(Quest.Status.COMPLETED);

        return true;
    }

    // Find a quest by ID.
    public static Quest findQuest(String questId) {

        for (Quest quest : quests) {

            if (quest.getId().equals(questId)) {
                return quest;
            }
        }

        return null;
    }

    // Add an additional quest.
    public static void addQuest(Quest quest) {

        if (findQuest(quest.getId()) != null) {
            return;
        }

        quests.add(quest);
    }
}
