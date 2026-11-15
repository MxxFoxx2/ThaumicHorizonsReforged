package dev.visfox.thaumichorizons.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SyringeBloodItem extends Item {
    public SyringeBloodItem(Properties properties) {
        super(properties);
    }

    public boolean isPhial(ItemStack stack) {
        SyringeKeys.BloodData data = SyringeKeys.BLOOD.get(stack);
        return data != null && data.phial();
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isPhial(stack)) {
            return Component.translatable("item.thaumichorizons.syringeBlood.1.name");
        }
        return Component.translatable("item.thaumichorizons.syringeBlood.0.name");
    }
}
