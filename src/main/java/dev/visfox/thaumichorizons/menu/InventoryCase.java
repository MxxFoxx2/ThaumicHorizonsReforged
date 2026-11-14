package dev.visfox.thaumichorizons.menu;

import dev.visfox.thaumichorizons.item.lenses.ILens;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class InventoryCase implements Container {
    private final ItemStack[] stackList = new ItemStack[18];
    private final AbstractContainerMenu menu;

    public InventoryCase(AbstractContainerMenu menu) {
        this.menu = menu;
        for (int i = 0; i < 18; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
    }

    public ItemStack[] getStackList() {
        return this.stackList;
    }

    @Override
    public int getContainerSize() {
        return this.stackList.length;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stackList) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.stackList.length ? this.stackList[slot] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (stack.getCount() <= amount) {
            this.stackList[slot] = ItemStack.EMPTY;
        } else {
            ItemStack split = stack.split(amount);
            if (this.stackList[slot].getCount() == 0) {
                this.stackList[slot] = ItemStack.EMPTY;
            }
            setChanged();
            return split;
        }
        setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        this.stackList[slot] = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.stackList[slot] = stack;
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.getItem() instanceof ILens;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void setChanged() {
        this.menu.slotsChanged(this);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.stackList.length; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
    }
}
