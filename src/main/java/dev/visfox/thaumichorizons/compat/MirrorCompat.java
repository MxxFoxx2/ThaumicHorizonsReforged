package dev.visfox.thaumichorizons.compat;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface MirrorCompat {
    default boolean isAvailable() {
        return false;
    }

    default boolean isMirror(Level level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default boolean isLinked(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default void link(ItemStack stack, Player player, Level level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default void appendLink(ItemStack stack, Consumer<Component> builder) {
        throw new UnsupportedOperationException();
    }

    default boolean transport(ItemStack mirror, ItemStack items, Player player, Level level) {
        throw new UnsupportedOperationException();
    }
}
