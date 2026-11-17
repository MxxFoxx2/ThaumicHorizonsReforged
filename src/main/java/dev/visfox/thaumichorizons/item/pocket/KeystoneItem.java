package dev.visfox.thaumichorizons.item.pocket;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import dev.visfox.thaumichorizons.world.pocket.PocketDimension;
import dev.visfox.thaumichorizons.world.pocket.PocketPlaneData;
import dev.visfox.thaumichorizons.world.pocket.PocketPlanes;
import dev.visfox.thaumichorizons.world.pocket.SlotBlockEntity;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;

public class KeystoneItem extends Item {
    public KeystoneItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static boolean isBound(ItemStack stack) {
        return stack.get(DataComponents.CUSTOM_DATA) != null;
    }

    public static int getPlaneID(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        return data.copyTag().getIntOr("dimension", 0);
    }

    private static CompoundTag boundData(int planeID) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("dimension", planeID);
        if (PocketPlanes.validPlane(planeID)) {
            PocketPlaneData plane = PocketPlanes.planes.get(planeID);
            tag.putString("name", plane.name);
            tag.putInt("color", plane.color);
        }
        return tag;
    }

    public static ItemStack create(int planeID) {
        ItemStack stack = new ItemStack(PocketRegistry.KEYSTONE_TH.get());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(boundData(planeID)));
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        var level = context.getLevel();
        var blockEntity = level.getBlockEntity(context.getClickedPos());

        if (!isBound(stack)) {
            if (!level.dimension().equals(PocketDimension.POCKET_PLANE)
                || !(blockEntity instanceof VortexBlockEntity vortex)
                || !vortex.createdDimension
                || !PocketPlanes.validPlane(vortex.dimensionID)) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(boundData(vortex.dimensionID)));
            }
            return InteractionResult.SUCCESS;
        }

        int planeID = getPlaneID(stack);
        if (!(blockEntity instanceof SlotBlockEntity slot) || !PocketPlanes.validPlane(planeID)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!slot.insertKeystone(planeID)) {
            return InteractionResult.PASS;
        }
        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
            net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> builder,
            TooltipFlag flag) {
        int planeID = getPlaneID(stack);
        if (isBound(stack) && PocketPlanes.validPlane(planeID)) {
            builder.accept(Component.translatable("item.thaumichorizons.keystoneth"));
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            String planeName = data == null ? "" : data.copyTag().getStringOr("name", "");
            if (!planeName.isBlank()) {
                builder.accept(Component.literal(planeName));
            }
        } else {
            builder.accept(Component.translatable("item.thaumichorizons.keystoneth_blank"));
        }
    }
}
