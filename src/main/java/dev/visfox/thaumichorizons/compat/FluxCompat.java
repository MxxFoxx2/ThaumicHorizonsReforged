package dev.visfox.thaumichorizons.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface FluxCompat {
    default boolean isAvailable() {
        return false;
    }

    default boolean spill(ServerLevel level, BlockPos pos, boolean gas) {
        throw new UnsupportedOperationException();
    }
}
