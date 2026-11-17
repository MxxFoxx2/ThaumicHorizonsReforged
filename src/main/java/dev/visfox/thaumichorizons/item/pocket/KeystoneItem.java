package dev.visfox.thaumichorizons.item.pocket;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class KeystoneItem extends Item {
    public KeystoneItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static int getPlaneID(ItemStack stack) {
        var data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        var tag = data.copyTag();
        return tag.getInt("dimension").orElse(0);
    }

    public static ItemStack create(int planeID) {
        ItemStack stack = new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(dev.visfox.thaumichorizons.Thaumichorizons.id("keystoneth")));
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(
            new net.minecraft.nbt.CompoundTag().putInt("dimension", planeID)));
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int planeID = getPlaneID(stack);
        if (planeID != 0) {
            tooltip.add(Component.translatable("item.thaumichorizons.keystoneth"));
        } else {
            tooltip.add(Component.translatable("item.thaumichorizons.keystoneth_blank"));
        }
    }
}