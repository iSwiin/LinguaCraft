
package com.lisa.linguacraft;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.util.datafix.DataFixTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerManager extends SavedData {

    public static final String DATA_NAME = "linguacraft_villagers";

    public static final SavedData.Factory<VillagerManager> FACTORY =
            new SavedData.Factory<>(
                    VillagerManager::new,
                    VillagerManager::load,
                    DataFixTypes.LEVEL
            );

    private final Map<UUID, VillagerProfile> villagers = new HashMap<>();

    public VillagerManager() {
    }

    public void registerVillager(VillagerProfile profile) {
        villagers.put(profile.getVillagerId(), profile);
        setDirty();
    }

    public VillagerProfile getVillager(UUID villagerId) {
        return villagers.get(villagerId);
    }

    // Find a LinguaCraft profile using a Minecraft villager entity.
    public VillagerProfile getProfileFor(Villager villager) {
        return villagers.get(villager.getUUID());
    }

    public boolean isRegistered(UUID villagerId) {
        return villagers.containsKey(villagerId);
    }

    public void removeVillager(UUID villagerId) {
        if (villagers.remove(villagerId) != null) {
            setDirty();
        }
    }

    public Collection<VillagerProfile> getAllVillagers() {
        return villagers.values();
    }

    public void updateWorkplace(UUID villagerId, BlockPos workplace) {
        VillagerProfile profile = villagers.get(villagerId);

        if (profile != null) {
            profile.setWorkplace(workplace);
            setDirty();
        }
    }

    public void updateHome(UUID villagerId, BlockPos home) {
        VillagerProfile profile = villagers.get(villagerId);

        if (profile != null) {
            profile.setHome(home);
            setDirty();
        }
    }

    public static VillagerManager load(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        VillagerManager manager = new VillagerManager();

        ListTag villagerList = tag.getList(
                "Villagers",
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < villagerList.size(); i++) {
            CompoundTag villagerTag = villagerList.getCompound(i);

            try {
                UUID villagerId = UUID.fromString(
                        villagerTag.getString("UUID")
                );

                VillagerRole role = VillagerRole.valueOf(
                        villagerTag.getString("Role")
                );

                BlockPos workplace = BlockPos.of(
                        villagerTag.getLong("Workplace")
                );

                BlockPos home = BlockPos.of(
                        villagerTag.getLong("Home")
                );

                VillagerProfile profile = new VillagerProfile(
                        villagerId,
                        role,
                        workplace,
                        home
                );

                manager.villagers.put(villagerId, profile);

            } catch (IllegalArgumentException exception) {
                LinguaCraft.LOGGER.error(
                        "Could not load a LinguaCraft villager profile",
                        exception
                );
            }
        }

        LinguaCraft.LOGGER.info(
                "Loaded {} LinguaCraft villager profiles",
                manager.villagers.size()
        );

        return manager;
    }

    @Override
    public CompoundTag save(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        ListTag villagerList = new ListTag();

        for (VillagerProfile profile : villagers.values()) {
            CompoundTag villagerTag = new CompoundTag();

            villagerTag.putString(
                    "UUID",
                    profile.getVillagerId().toString()
            );

            villagerTag.putString(
                    "Role",
                    profile.getRole().name()
            );

            villagerTag.putLong(
                    "Workplace",
                    profile.getWorkplace().asLong()
            );

            villagerTag.putLong(
                    "Home",
                    profile.getHome().asLong()
            );

            villagerList.add(villagerTag);
        }

        tag.put("Villagers", villagerList);

        return tag;
    }
}
