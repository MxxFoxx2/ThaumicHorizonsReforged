package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.blockentity.VatBlockEntity;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.registry.ThMenus;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public class VatMenu extends AbstractContainerMenu {
    private final VatBlockEntity tile;
    @Nullable
    private final SampleSlot slotSample;
    private final Player player;

    public VatMenu(int containerId, Inventory inventory, VatBlockEntity tile) {
        super(ThMenus.VAT.get(), containerId);
        this.tile = tile;
        this.player = inventory.player;
        if (Compat.port().research().isComplete(inventory.player, "incarnationVat")) {
            addSlot(this.slotSample = new SampleSlot(tile, 0, 63, 32));
            addSlot(new RestrictedSlot(tile, 1, 96, 32,
                stack -> stack.is(ThItems.NUTRIENTS.get())));
        } else {
            this.slotSample = null;
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 127 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 185));
        }
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
            if (slotSample != null && index == 0) {
                if (!moveItemStackTo(itemstack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack2, itemstack);
            } else if (slotSample != null && index == 1) {
                if (!moveItemStackTo(itemstack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack2, itemstack);
            } else {
                if (slotSample == null) {
                    return ItemStack.EMPTY;
                }
                if (itemstack2.is(ThItems.NUTRIENTS.get()) && moveItemStackTo(itemstack2, 1, 2, false)
                    || slotSample.mayPlace(itemstack2) && moveItemStackTo(itemstack2, 0, 1, false)) {
                    if (itemstack2.isEmpty()) {
                        slot.setByPlayer(ItemStack.EMPTY);
                    } else {
                        slot.setChanged();
                    }
                    return itemstack;
                }
                return ItemStack.EMPTY;
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

    public VatBlockEntity tile() {
        return tile;
    }

    public Player player() {
        return player;
    }

    public @Nullable SampleSlot sampleSlot() {
        return slotSample;
    }
}
