package dev.visfox.thaumichorizons.item.pocket;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

public class KeystoneItem extends Item {
    public KeystoneItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static int getPlaneID(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        return data.copyTag().getIntOr("dimension", 0);
    }

    public static ItemStack create(int planeID) {
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(ThaumicHorizons.id("keystoneth")));
        CompoundTag tag = new CompoundTag();
        tag.putInt("dimension", planeID);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
            net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> builder,
            TooltipFlag flag) {
        int planeID = getPlaneID(stack);
        if (planeID != 0) {
            builder.accept(Component.translatable("item.thaumichorizons.keystoneth"));
        } else {
            builder.accept(Component.translatable("item.thaumichorizons.keystoneth_blank"));
        }
    }
}