package com.lisa.linguacraft;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerManager {

    private final Map<UUID, VillagerProfile> villagers = new HashMap<>();

    public void registerVillager(VillagerProfile profile) {
        villagers.put(profile.getVillagerId(), profile);
    }

    public VillagerProfile getVillager(UUID villagerId) {
        return villagers.get(villagerId);
    }

    public boolean isRegistered(UUID villagerId) {
        return villagers.containsKey(villagerId);
    }

    public void removeVillager(UUID villagerId) {
        villagers.remove(villagerId);
    }

    public Collection<VillagerProfile> getAllVillagers() {
        return villagers.values();
    }

    public void updateWorkplace(UUID villagerId, net.minecraft.core.BlockPos workplace) {
        VillagerProfile profile = villagers.get(villagerId);

        if (profile != null) {
            profile.setWorkplace(workplace);
        }
    }

    public void updateHome(UUID villagerId, net.minecraft.core.BlockPos home) {
        VillagerProfile profile = villagers.get(villagerId);

        if (profile != null) {
            profile.setHome(home);
        }
    }
}
