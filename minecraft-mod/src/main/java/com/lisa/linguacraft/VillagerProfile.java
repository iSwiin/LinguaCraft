package com.lisa.linguacraft;

import net.minecraft.core.BlockPos;

import java.util.UUID;

public class VillagerProfile {

    private final UUID villagerId;
    private final VillagerRole role;

    private BlockPos workplace;
    private BlockPos home;

    public VillagerProfile(
            UUID villagerId,
            VillagerRole role,
            BlockPos workplace,
            BlockPos home
    ) {
        this.villagerId = villagerId;
        this.role = role;
        this.workplace = workplace;
        this.home = home;
    }

    public UUID getVillagerId() {
        return villagerId;
    }

    public VillagerRole getRole() {
        return role;
    }

    public BlockPos getWorkplace() {
        return workplace;
    }

    public BlockPos getHome() {
        return home;
    }

    public void setWorkplace(BlockPos workplace) {
        this.workplace = workplace;
    }

    public void setHome(BlockPos home) {
        this.home = home;
    }
}
