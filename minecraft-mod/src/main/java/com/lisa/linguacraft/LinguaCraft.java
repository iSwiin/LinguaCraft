
package com.lisa.linguacraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinguaCraft implements ModInitializer {

	public static final String MOD_ID = "linguacraft";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	private static VillagerManager villagerManager;

	@Override
	public void onInitialize() {

		// Load persistent villager data when the world starts.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {

			villagerManager = server.overworld()
					.getDataStorage()
					.computeIfAbsent(
							VillagerManager.FACTORY,
							VillagerManager.DATA_NAME
					);

			LOGGER.info(
					"LinguaCraft villager manager initialized with {} saved profiles!",
					villagerManager.getAllVillagers().size()
			);
		});

		// Recognize LinguaCraft villagers when Minecraft loads them.
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {

			if (!(entity instanceof Villager villager)) {
				return;
			}

			VillagerManager manager = level.getServer()
					.overworld()
					.getDataStorage()
					.computeIfAbsent(
							VillagerManager.FACTORY,
							VillagerManager.DATA_NAME
					);

			VillagerProfile profile =
					manager.getProfileFor(villager);

			if (profile != null) {

				LOGGER.info(
						"Recognized LinguaCraft villager: {} | Role: {}",
						villager.getName().getString(),
						profile.getRole().getDisplayName()
				);
			}
		});

		// Clear the manager reference when the world closes.
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {

			villagerManager = null;

			LOGGER.info("LinguaCraft villager manager stopped!");
		});

		// Register LinguaCraft commands.
		CommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess, environment) -> {

					dispatcher.register(
							Commands.literal("linguacraft")
									.then(Commands.literal("spawn")
											.executes(context -> {

												ServerPlayer player =
														context.getSource()
																.getPlayerOrException();

												VillagerManager manager =
														getVillagerManager();

												if (manager == null) {
													context.getSource().sendFailure(
															Component.literal(
																	"LinguaCraft is not ready yet."
															)
													);
													return 0;
												}

												if (!manager.getAllVillagers().isEmpty()) {
													context.getSource().sendFailure(
															Component.literal(
																	"LinguaCraft villagers have already been registered in this world."
															)
													);
													return 0;
												}

												ServerLevel level =
														player.serverLevel();

												BlockPos center =
														player.blockPosition();

												VillagerSpawner.spawnStartingVillagers(
														level,
														center
												);

												context.getSource().sendSuccess(
														() -> Component.literal(
																"Your Baker, Cafe Owner, and Farmer have arrived!"
														),
														false
												);

												return 1;
											})
									)
					);
				}
		);

		LOGGER.info("LinguaCraft initialized!");
	}

	public static VillagerManager getVillagerManager() {
		return villagerManager;
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(
				MOD_ID,
				path
		);
	}
}
