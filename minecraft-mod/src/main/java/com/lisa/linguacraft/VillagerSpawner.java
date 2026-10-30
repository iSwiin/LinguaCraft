
package com.lisa.linguacraft;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;

public class VillagerSpawner {

    public static void spawnStartingVillagers(
            ServerLevel level,
            BlockPos center
    ) {

        spawnVillager(
                level,
                center.offset(3, 0, 0),
                VillagerRole.BAKER,
                "Baker"
        );

        spawnVillager(
                level,
                center.offset(-3, 0, 0),
                VillagerRole.CAFE_OWNER,
                "Cafe Owner"
        );

        spawnVillager(
                level,
                center.offset(0, 0, 3),
                VillagerRole.FARMER,
                "Farmer"
        );

        LinguaCraft.LOGGER.info(
                "Spawned LinguaCraft starting villagers!"
        );
    }

    private static void spawnVillager(
            ServerLevel level,
            BlockPos position,
            VillagerRole role,
            String name
    ) {

        Villager villager = EntityType.VILLAGER.create(level);

        if (villager == null) {
            LinguaCraft.LOGGER.error(
                    "Could not create villager: " + name
            );
            return;
        }

        villager.moveTo(
                position.getX() + 0.5,
                position.getY(),
                position.getZ() + 0.5,
                0.0F,
                0.0F
        );

        villager.setCustomName(Component.literal(name));
        villager.setCustomNameVisible(true);

        boolean added = level.addFreshEntity(villager);

        if (!added) {
            LinguaCraft.LOGGER.error(
                    "Could not add villager: " + name
            );
            return;
        }

        VillagerProfile profile = new VillagerProfile(
                villager.getUUID(),
                role,
                position,
                position
        );

        VillagerManager manager =
                LinguaCraft.getVillagerManager();

        if (manager != null) {
            manager.registerVillager(profile);

            LinguaCraft.LOGGER.info(
                    "Registered {} with LinguaCraft role {}",
                    name,
                    role
            );
        }
    }
}
