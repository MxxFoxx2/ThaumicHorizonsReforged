package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.blockentity.SoulExtractorBlockEntity;
import dev.visfox.thaumichorizons.registry.ThMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SoulExtractorMenu extends AbstractContainerMenu {
    private final SoulExtractorBlockEntity tile;
    private final ContainerData data;

    public SoulExtractorMenu(int containerId, Inventory inventory, SoulExtractorBlockEntity tile) {
        super(ThMenus.SOUL_SIEVE.get(), containerId);
        this.tile = tile;
        this.data = new SimpleContainerData(1);
        addSlot(new RestrictedSlot(tile.sieveInventory, 0, 64, 30, stack -> stack.is(Items.SOUL_SAND)));
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
        addDataSlots(data);
    }

    public int getTimeRemainingScaled(int scale) {
        return tile.getTimeRemainingScaled(scale);
    }

    public SoulExtractorBlockEntity tile() {
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
            if (index == 0) {
                if (!moveItemStackTo(itemstack2, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack2, itemstack);
            } else {
                if (!itemstack2.is(Items.SOUL_SAND) || !moveItemStackTo(itemstack2, 0, 1, false)) {
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
