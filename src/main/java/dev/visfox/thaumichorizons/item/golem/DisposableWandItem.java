package dev.visfox.thaumichorizons.item.golem;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.wand.WandCap;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.WandKeys;
import thaumcraft.wand.WandParts;
import thaumcraft.wand.WandRegistry;
import thaumcraft.wand.WandRod;
import thaumcraft.wand.WandVis;
import dev.visfox.thaumichorizons.ThaumicHorizons;

public class DisposableWandItem extends WandItem {
    public static final WandCap CAP_DISPOSABLE = WandRegistry.registerCap(
        "disposable", 0.9F, List.of(), 0.0F, ResourceLocation.withDefaultNamespace("bedrock"), 99, null,
        ThaumicHorizons.id("textures/models/crystalcap.png"));
    public static final WandRod ROD_DISPOSABLE = WandRegistry.registerRod(
        "disposable", 250, ResourceLocation.withDefaultNamespace("bedrock"), 99, null, true, false, false,
        ThaumicHorizons.id("textures/models/crystalrod.png"));

    public DisposableWandItem(Properties properties) {
        super(properties);
    }

    public static Item.Properties properties(Item.Properties properties) {
        Item.Properties result = WandKeys.PARTS.apply(WandItem.properties(properties),
            new WandParts(CAP_DISPOSABLE.tag(), ROD_DISPOSABLE.tag(), false));
        return WandKeys.VIS.apply(result, new WandVis(25000, 25000, 25000, 25000, 25000, 25000));
    }

    public static ItemStack create() {
        return new ItemStack(BuiltInRegistries.ITEM.get(ThaumicHorizons.id("wandcastingdisposable")));
    }
}
