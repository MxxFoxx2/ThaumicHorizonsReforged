package dev.visfox.thaumichorizons.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface FxCompat {
    default boolean isAvailable() {
        return false;
    }

    default void sparkle(Level level, float x, float y, float z, float size, int color, float gravity) {
        throw new UnsupportedOperationException();
    }

    default void wisp(Level level, Vec3 from, Vec3 to, int color, boolean longer) {
        throw new UnsupportedOperationException();
    }

    default void burst(Level level, Vec3 pos, int color, int count) {
        throw new UnsupportedOperationException();
    }

    default void arcLightning(Level level, Vec3 from, Vec3 to, int color) {
        throw new UnsupportedOperationException();
    }

    default void blockSparkle(Level level, BlockPos pos, int color, int count) {
        throw new UnsupportedOperationException();
    }

    default void essentiaTrail(Level level, BlockPos from, Vec3 to, int color) {
        throw new UnsupportedOperationException();
    }

    default void blocksplosion(Level level, BlockPos center, int color) {
        throw new UnsupportedOperationException();
    }

    default void containmentCapture(Level level, Vec3 pos, int color) {
        throw new UnsupportedOperationException();
    }

    default void essentiaBubble(Level level, BlockPos pos, int color) {
        throw new UnsupportedOperationException();
    }

    default void deadCreatureFx(Level level, Vec3 pos, int color) {
        throw new UnsupportedOperationException();
    }
}
