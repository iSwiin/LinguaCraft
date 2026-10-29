package com.lisa.linguacraft.client;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;

public class VillagerInteraction {

    public static void register() {
        UseEntityCallback.EVENT.register(
                (player, world, hand, entity, hitResult) -> {

                    if (entity instanceof Villager && world.isClientSide()) {
                        Minecraft.getInstance().setScreen(
                                new VillagerDialogueScreen()
                        );

                        return InteractionResult.SUCCESS;
                    }

                    return InteractionResult.PASS;
                }
        );
    }
}
