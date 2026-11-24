package dev.visfox.thaumichorizons.lib;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import javax.annotation.Nullable;

public final class TcBlocks {
    private TcBlocks() {
    }

    public static @Nullable Block block(String name) {
        return BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath("thaumcraft", name)).orElse(null);
    }

    public static @Nullable Item item(String name) {
        return TcReforgedPort.items.lookup(ResourceLocation.fromNamespaceAndPath("thaumcraft", name));
    }

    public static ItemStack emptyJar() {
        Item jar = item("warded_jar");
        return jar == null ? ItemStack.EMPTY : new ItemStack(jar);
    }
}
