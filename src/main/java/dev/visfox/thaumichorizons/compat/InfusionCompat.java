package dev.visfox.thaumichorizons.compat;

import net.minecraft.world.entity.player.Player;

public interface InfusionCompat {
    default boolean isAvailable() {
        return false;
    }

    default boolean isInfusionStable(Player player) {
        throw new UnsupportedOperationException();
    }

    default void registerStabilizerPredicate(StabilizerPredicate predicate) {
        throw new UnsupportedOperationException();
    }

    public interface StabilizerPredicate {
        boolean isStabilizer(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    }
}
