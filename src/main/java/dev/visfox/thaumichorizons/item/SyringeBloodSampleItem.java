package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.infusion.ThInfusionRecipes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;

public class SyringeBloodSampleItem extends Item {
    public SyringeBloodSampleItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        SyringeKeys.SampleData data = SyringeKeys.SAMPLE.get(stack);
        if (data != null && !data.critterName().isEmpty()) {
            return Component.translatable("item.thaumichorizons.syringeSample.name")
                .append(": " + data.critterName());
        }
        return Component.translatable("item.thaumichorizons.syringeSample.name").append(": INVALID");
    }

    @Override
    public void appendHoverText(ItemStack sample, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display,
        java.util.function.Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.literal("Essentia required to clone:").withStyle(ChatFormatting.GRAY));
        AspectList asp = new AspectList().add(Aspect.LIFE, 4);
        SyringeKeys.SampleData data = SyringeKeys.SAMPLE.get(sample);
        if (data != null) {
            for (int infusion : data.infusions()) {
                for (ThInfusionRecipes.CreatureInfusionRecipe recipe : ThInfusionRecipes.getCritterRecipes()) {
                    if (recipe.getID() == infusion) {
                        asp.add(recipe.getAspects());
                    }
                }
            }
        }
        for (Aspect tag : asp.getAspectsSorted()) {
            tooltip.accept(Component.literal(tag.getName() + " x" + asp.getAmount(tag)).withStyle(ChatFormatting.GRAY));
        }
    }
}
