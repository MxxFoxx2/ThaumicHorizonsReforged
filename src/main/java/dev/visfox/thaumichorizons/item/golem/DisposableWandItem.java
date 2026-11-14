package dev.visfox.thaumichorizons.item.golem;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.aspect.Aspect;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.WandKeys;
import thaumcraft.wand.WandRegistry;
import thaumcraft.wand.WandCap;
import thaumcraft.wand.WandRod;
import thaumcraft.wand.Wands;
import dev.visfox.thaumichorizons.ThaumicHorizons;

public class DisposableWandItem extends WandItem {
    public static final WandCap CAP_DISPOSABLE = WandRegistry.registerCap(
        "disposable", 0.9F, List.of(), 0.0F, Identifier.withDefaultNamespace("bedrock"), 99, null,
        ThaumicHorizons.id("textures/models/crystalcap.png"));
    public static final WandRod ROD_DISPOSABLE = WandRegistry.registerRod(
        "disposable", 250, Identifier.withDefaultNamespace("bedrock"), 99, null, true, false, false,
        ThaumicHorizons.id("textures/models/crystalrod.png"));

    public DisposableWandItem(Properties properties) {
        super(properties);
    }

    public static ItemStack create() {
        ItemStack template = Wands.create(CAP_DISPOSABLE, ROD_DISPOSABLE, false);
        var item = BuiltInRegistries.ITEM.getValue(ThaumicHorizons.id("wandcastingdisposable"));
        if (item == null) {
            return template;
        }
        ItemStack stack = new ItemStack(item);
        WandKeys.PARTS.set(stack, Wands.getParts(template));
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            Wands.storeVis(stack, aspect, 25000);
        }
        return stack;
    }
}
