
package com.lisa.linguacraft.client;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;

public class VillagerInteraction {

    public static void register() {

        UseEntityCallback.EVENT.register(
                (player, world, hand, entity, hitResult) -> {

                    if (entity instanceof Villager villager
                            && world.isClientSide()) {

                        String villagerRole =
                                villager.getName().getString();

                        Minecraft.getInstance().setScreen(
                                new VillagerDialogueScreen(villagerRole)
                        );

                        return InteractionResult.SUCCESS;
                    }

                    return InteractionResult.PASS;
                }
        );
    }
}
