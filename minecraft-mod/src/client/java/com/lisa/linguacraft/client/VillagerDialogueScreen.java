
package com.lisa.linguacraft.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.lisa.linguacraft.Quest;
import com.lisa.linguacraft.QuestManager;
import com.lisa.linguacraft.VillagerRole;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VillagerDialogueScreen extends Screen {

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newHttpClient();

    private final String villagerRole;

    private EditBox messageBox;

    private String responseText =
            "こんにちは！日本語で話しましょう。";

    public VillagerDialogueScreen(String villagerRole) {
        super(Component.literal(
                "LinguaCraft - " + villagerRole
        ));

        this.villagerRole = villagerRole;
    }

    @Override
    protected void init() {

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        messageBox = new EditBox(
                this.font,
                centerX - 150,
                centerY + 45,
                300,
                20,
                Component.literal("日本語で入力...")
        );

        messageBox.setMaxLength(300);
        addRenderableWidget(messageBox);

        addRenderableWidget(
                Button.builder(
                                Component.literal("送信 (Send)"),
                                button -> sendMessage()
                        )
                        .bounds(centerX - 50, centerY + 75, 100, 20)
                        .build()
        );
    }

    private void sendMessage() {

        String message = messageBox.getValue().trim();

        if (message.isEmpty()) {
            return;
        }

        // Check quest objectives locally.
        List<String> progressMessages =
                checkQuestProgress(message);

        responseText = "考え中... (Thinking)";
        messageBox.setValue("");

        // Send the message to Meta AI.
        JsonObject jsonBody = new JsonObject();

        jsonBody.addProperty("message", message);
        jsonBody.addProperty("level", "beginner");
        jsonBody.addProperty("villager_role", villagerRole);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/talk"))
                .header("Content-Type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                jsonBody.toString()
                        )
                )
                .build();

        HTTP_CLIENT.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString(
                                StandardCharsets.UTF_8
                        )
                )
                .thenApply(httpResponse -> {

                    if (httpResponse.statusCode() != 200) {
                        throw new RuntimeException(
                                "Backend returned HTTP "
                                        + httpResponse.statusCode()
                        );
                    }

                    JsonObject json = JsonParser.parseString(
                            httpResponse.body()
                    ).getAsJsonObject();

                    return json.get("reply").getAsString();
                })
                .exceptionally(error ->
                        "Connection error: " + error.getMessage()
                )
                .thenAccept(reply ->
                        Minecraft.getInstance().execute(() -> {

                            responseText = reply;

                            if (!progressMessages.isEmpty()) {

                                responseText += "\n\n";

                                for (String progress : progressMessages) {
                                    responseText += progress + "\n";
                                }

                                responseText +=
                                        "\nYour skill tree has been updated!";
                            }
                        })
                );
    }

    private List<String> checkQuestProgress(String message) {

        List<String> messages = new ArrayList<>();

        String normalized = normalize(message);

        VillagerRole role = getVillagerRole();

        if (role == null) {
            return messages;
        }

        for (Quest quest : QuestManager.getActiveQuests()) {

            if (quest.getVillagerRole() != role) {
                continue;
            }

            for (int i = 0; i < quest.getTotalObjectiveCount(); i++) {

                if (quest.isObjectiveCompleted(i)) {
                    continue;
                }

                if (!matchesObjective(
                        quest.getId(),
                        i,
                        normalized
                )) {
                    continue;
                }

                boolean wasLastObjective =
                        quest.getCompletedObjectiveCount()
                                == quest.getTotalObjectiveCount() - 1;

                boolean completed = QuestManager.completeObjective(
                        quest.getId(),
                        i
                );

                if (!completed) {
                    continue;
                }

                String objective = quest.getObjectives().get(i);

                messages.add("Objective completed: " + objective);

                if (wasLastObjective
                        && quest.getStatus() == Quest.Status.COMPLETED) {

                    messages.add(
                            "QUEST COMPLETED: " + quest.getTitle()
                    );

                    messages.add(
                            "Reward: " + quest.getReward()
                    );
                }
            }
        }

        return messages;
    }

    private boolean matchesObjective(
            String questId,
            int objectiveIndex,
            String message
    ) {

        return switch (questId) {

            // BAKER

            case "baker_greeting",
                 "cafe_greeting",
                 "farmer_greeting" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"こんにちは", "こんにちわ", "konnichiwa"},
                    new String[]{"こんにちは", "こんにちわ", "konnichiwa"}
            );

            case "baker_food" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"パン", "pan"},
                    new String[]{"ケーキ", "keeki", "cake"}
            );

            case "baker_order" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"パンをください", "パンください", "panwokudasai"},
                    new String[]{"パンをください", "パンください", "panwokudasai"}
            );

            case "baker_ingredients" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"たまご", "卵", "tamago"},
                    new String[]{"こむぎこ", "小麦粉", "komugiko"}
            );


            // CAFE OWNER

            case "cafe_drinks" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"コーヒー", "koohii", "coffee"},
                    new String[]{"おちゃ", "お茶", "ocha"}
            );

            case "cafe_order" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"コーヒーをください", "コーヒーください", "koohiowokudasai"},
                    new String[]{"コーヒーをください", "コーヒーください", "koohiowokudasai"}
            );

            case "cafe_conversation" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"ありがとう", "arigatou", "arigato"},
                    new String[]{"ありがとう", "arigatou", "arigato"}
            );


            // FARMER

            case "farmer_vegetables" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"にんじん", "人参", "ninjin"},
                    new String[]{"じゃがいも", "ジャガイモ", "jagaimo"}
            );

            case "farmer_numbers" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"いち", "一", "ichi"},
                    new String[]{"に", "二", "ni"},
                    new String[]{"さん", "三", "san"}
            );

            case "farmer_seasons" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"はる", "春", "haru"},
                    new String[]{"なつ", "夏", "natsu"}
            );

            case "farmer_trade" -> matches(
                    objectiveIndex,
                    message,
                    new String[]{"やさい", "野菜", "yasai"},
                    new String[]{"やさいをください", "yasaiwokudasai"},
                    new String[]{"やさいをください", "yasaiwokudasai"}
            );

            default -> false;
        };
    }

    // Checks the phrase list belonging to one objective.
    private boolean matches(
            int objectiveIndex,
            String message,
            String[]... objectivePhrases
    ) {

        if (objectiveIndex < 0
                || objectiveIndex >= objectivePhrases.length) {
            return false;
        }

        for (String phrase : objectivePhrases[objectiveIndex]) {

            if (message.contains(normalize(phrase))) {
                return true;
            }
        }

        return false;
    }

    private String normalize(String message) {

        return message
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "")
                .replaceAll("[。、！？!?,.]", "");
    }

    private VillagerRole getVillagerRole() {

        return switch (villagerRole) {
            case "Baker" -> VillagerRole.BAKER;
            case "Cafe Owner" -> VillagerRole.CAFE_OWNER;
            case "Farmer" -> VillagerRole.FARMER;
            default -> null;
        };
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(
                this.font,
                villagerRole,
                this.width / 2,
                this.height / 2 - 75,
                0xFFFF55
        );

        List<net.minecraft.util.FormattedCharSequence> lines =
                this.font.split(
                        Component.literal(responseText),
                        290
                );

        int responseY = this.height / 2 - 45;

        for (int i = 0; i < Math.min(lines.size(), 7); i++) {

            graphics.drawString(
                    this.font,
                    lines.get(i),
                    this.width / 2 - 145,
                    responseY + i * 12,
                    0xFFFFFF
            );
        }
    }
}
