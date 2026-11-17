package dev.visfox.thaumichorizons.compat;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import org.jspecify.annotations.Nullable;

public interface ItemCompat {
    default @Nullable Item lookup(Identifier id) {
        throw new UnsupportedOperationException();
    }

    default boolean isWand(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default boolean isEssentiaJar(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default AspectList getAspects(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default void setSingleAspect(ItemStack stack, Aspect aspect, int amount) {
        throw new UnsupportedOperationException();
    }
}
