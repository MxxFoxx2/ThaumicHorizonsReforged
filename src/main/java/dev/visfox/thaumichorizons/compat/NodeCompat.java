package dev.visfox.thaumichorizons.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface NodeCompat {
    default boolean isAvailable() {
        return false;
    }

    default boolean tryRecombine(ServerLevel level, BlockPos machinePos, BlockPos targetPos) {
        return false;
    }

    default void stopTransducerForVortexConversion(ServerLevel level, BlockPos transducerPos) {
        throw new UnsupportedOperationException();
    }
}
