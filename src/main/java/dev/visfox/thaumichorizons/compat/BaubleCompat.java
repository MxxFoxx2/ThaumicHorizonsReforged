package dev.visfox.thaumichorizons.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface BaubleCompat {
    default boolean isAvailable() {
        return false;
    }

    default int slotCount() {
        throw new UnsupportedOperationException();
    }

    default ItemStack get(Player player, int slot) {
        throw new UnsupportedOperationException();
    }

    default void set(Player player, int slot, ItemStack stack) {
        throw new UnsupportedOperationException();
    }
}
