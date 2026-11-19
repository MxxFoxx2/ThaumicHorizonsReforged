package dev.visfox.thaumichorizons.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;

public interface InfusionCompat {
    default boolean isAvailable() {
        return false;
    }

    default boolean isStabiliser(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof AbstractSkullBlock;
    }

    default boolean isStabiliser(Level level, BlockPos blockPos, BlockPos stabiliserPos) {
        return isStabiliser(level, blockPos);
    }
}
