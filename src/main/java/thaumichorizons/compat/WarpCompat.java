package thaumichorizons.compat;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public interface WarpCompat {
    default boolean isAvailable() {
        return false;
    }

    default int get(Player player, WarpKind kind) {
        throw new UnsupportedOperationException();
    }

    default int total(Player player) {
        throw new UnsupportedOperationException();
    }

    default void add(Player player, int amount, WarpKind kind) {
        throw new UnsupportedOperationException();
    }

    default void removePermanent(Player player, int amount) {
        throw new UnsupportedOperationException();
    }

    default void registerItemWarp(Identifier item, int warp) {
        throw new UnsupportedOperationException();
    }
}
