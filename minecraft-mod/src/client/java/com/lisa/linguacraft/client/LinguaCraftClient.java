package com.lisa.linguacraft.client;

import net.fabricmc.api.ClientModInitializer;

public class LinguaCraftClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		VillagerInteraction.register();
	}
}
