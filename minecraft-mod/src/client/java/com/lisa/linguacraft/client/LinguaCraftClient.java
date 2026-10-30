
package com.lisa.linguacraft.client;

import com.lisa.linguacraft.client.QuestJournalScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class LinguaCraftClient implements ClientModInitializer {

	private static KeyMapping openQuestJournalKey;

	@Override
	public void onInitializeClient() {
		// Keep the existing villager interaction.
		VillagerInteraction.register();

		// Register J as the Quest Journal shortcut.
		openQuestJournalKey = KeyBindingHelper.registerKeyBinding(
				new KeyMapping(
						"key.linguacraft.open_quests",
						InputConstants.Type.KEYSYM,
						GLFW.GLFW_KEY_J,
						"key.categories.misc"
				)
		);

		// Open the journal when J is pressed.
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openQuestJournalKey.consumeClick()) {
				if (client.player != null) {
					client.setScreen(new QuestJournalScreen());
				}
			}
		});
	}
}
