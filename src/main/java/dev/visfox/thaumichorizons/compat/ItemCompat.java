package dev.visfox.thaumichorizons.compat;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import org.jspecify.annotations.Nullable;

public interface ItemCompat {
    default boolean isAvailable() {
        return false;
    }

    default @Nullable Item getSmeltingBonus(ItemStack input) {
        throw new UnsupportedOperationException();
    }

    default @Nullable Item lookup(Identifier id) {
        throw new UnsupportedOperationException();
    }

    default boolean isWand(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default boolean consumeVis(ItemStack stack, Player player, Aspect aspect, int amount, boolean crafting) {
        throw new UnsupportedOperationException();
    }

    default boolean isEssentiaJar(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default AspectList getAspects(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default @Nullable AspectList getObjectAspects(ItemStack stack) {
        return null;
    }

    default void setSingleAspect(ItemStack stack, Aspect aspect, int amount) {
        throw new UnsupportedOperationException();
    }
}
