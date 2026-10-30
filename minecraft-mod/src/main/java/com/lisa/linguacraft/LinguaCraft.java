package com.lisa.linguacraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinguaCraft implements ModInitializer {

	public static final String MOD_ID = "linguacraft";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	private static VillagerManager villagerManager;

	@Override
	public void onInitialize() {

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			villagerManager = new VillagerManager();

			LOGGER.info("LinguaCraft villager manager initialized!");
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			villagerManager = null;

			LOGGER.info("LinguaCraft villager manager stopped!");
		});

		LOGGER.info("LinguaCraft initialized!");
	}

	public static VillagerManager getVillagerManager() {
		return villagerManager;
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
