package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.blockentity.InspiratronBlockEntity;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.registry.ThMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class InspiratronMenu extends AbstractContainerMenu {
    private static final ResourceLocation KNOWLEDGE_FRAGMENT = ResourceLocation.fromNamespaceAndPath("thaumcraft", "knowledge_fragment");
    private final InspiratronBlockEntity tile;

    public InspiratronMenu(int containerId, Inventory inventory, InspiratronBlockEntity tile) {
        super(ThMenus.INSPIRATRON.get(), containerId);
        this.tile = tile;
        addSlot(new RestrictedSlot(tile, 0, 15, 42, stack -> stack.is(Items.PAPER)));
        addSlot(new RestrictedSlot(tile, 1, 146, 42, stack -> {
            net.minecraft.world.item.Item knowledge = TcReforgedPort.items.lookup(KNOWLEDGE_FRAGMENT);
            return knowledge != null && stack.is(knowledge);
        }));
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 137 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 195));
        }
    }

    public int getTimeRemainingScaled(int scale) {
        return tile.getTimeRemainingScaled(scale);
    }

    public InspiratronBlockEntity tile() {
        return tile;
    }

    @Override
    public boolean stillValid(Player player) {
        return tile.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.copy();
            if (index == 0 || index == 1) {
                if (!moveItemStackTo(itemstack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack2, itemstack);
            } else {
                if (!moveItemStackTo(itemstack2, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack2, itemstack);
            }
            if (itemstack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
        }
        return itemstack;
    }
}
