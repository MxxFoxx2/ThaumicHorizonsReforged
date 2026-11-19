package dev.visfox.thaumichorizons.compat;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface BrainJarCompat {
    default boolean isAvailable() {
        return false;
    }

    default boolean isBrainJar(BlockEntity entity) {
        return false;
    }

    default int xp(BlockEntity entity) {
        return 0;
    }

    default int maxXp() {
        return 0;
    }

    default void setXp(BlockEntity entity, int xp) {
    }
}
