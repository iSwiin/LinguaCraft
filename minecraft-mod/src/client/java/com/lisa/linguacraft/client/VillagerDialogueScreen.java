package com.lisa.linguacraft.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class VillagerDialogueScreen extends Screen {

    private EditBox messageBox;

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
                centerY + 10,
                300,
                20,
                Component.literal("日本語で話してみましょう...")
        );

        messageBox.setMaxLength(300);
        addRenderableWidget(messageBox);

        addRenderableWidget(
                Button.builder(
                        Component.literal("送信 (Send)"),
                        button -> {
                            // We'll connect this button to Meta AI next.
                            messageBox.setValue("");
                        }
                ).bounds(centerX - 50, centerY + 40, 100, 20).build()
        );
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
                "こんにちは！日本語で話しましょう。",
                this.width / 2,
                this.height / 2 - 30,
                0xFFFFFF
        );
    }
}
