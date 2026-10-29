
package com.lisa.linguacraft.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.List;

public class VillagerDialogueScreen extends Screen {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private EditBox messageBox;
    private String responseText = "こんにちは！日本語で話しましょう。";

    public VillagerDialogueScreen() {
        super(Component.literal("LinguaCraft - 日本語会話"));
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
                ).bounds(centerX - 50, centerY + 75, 100, 20).build()
        );
    }

    private void sendMessage() {
        String message = messageBox.getValue().trim();

        if (message.isEmpty()) {
            return;
        }

        responseText = "考え中... (Thinking)";
        messageBox.setValue("");

        String jsonBody = "{\"message\":\""
                + escapeJson(message)
                + "\",\"level\":\"beginner\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/talk"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HTTP_CLIENT.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                )
                .thenApply(httpResponse -> {
                    if (httpResponse.statusCode() != 200) {
                        throw new RuntimeException(
                                "Backend returned HTTP " + httpResponse.statusCode()
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
                        Minecraft.getInstance().execute(() -> responseText = reply)
                );
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
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
                "こんにちは！",
                this.width / 2,
                this.height / 2 - 75,
                0xFFFFFF
        );

        List<net.minecraft.util.FormattedCharSequence> lines =
                this.font.split(Component.literal(responseText), 290);

        int responseY = this.height / 2 - 45;

        for (int i = 0; i < Math.min(lines.size(), 5); i++) {
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
