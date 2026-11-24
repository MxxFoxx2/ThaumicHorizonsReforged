package dev.visfox.thaumichorizons.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;

public class FingersInventory implements Container {
    public static final int GRID_SLOTS = 9;
    public static final int OUTPUT_SLOT = 9;
    public static final int WAND_SLOT = 10;
    public static final int SLOT_COUNT = 11;

    private final ItemStack[] stacks = new ItemStack[SLOT_COUNT];

    public FingersInventory() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            stacks[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot < SLOT_COUNT ? stacks[slot] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (stack.getCount() <= amount) {
            setItem(slot, ItemStack.EMPTY);
            return stack;
        }
        ItemStack split = stack.split(amount);
        if (stack.getCount() == 0) {
            setItem(slot, ItemStack.EMPTY);
        }
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        stacks[slot] = stack;
        setChanged();
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            stacks[i] = ItemStack.EMPTY;
        }
    }


}
