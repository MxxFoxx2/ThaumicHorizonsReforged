package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InjectorInventory implements Container {
    private final ItemStack[] stacks = new ItemStack[InjectorMenu.AMMO_SLOTS];

    public InjectorInventory() {
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = ItemStack.EMPTY;
        }
    }

    public void setItemRange(java.util.List<ItemStack> items) {
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = i < items.size() ? items.get(i) : ItemStack.EMPTY;
        }
    }

    @Override
    public int getContainerSize() {
        return InjectorMenu.AMMO_SLOTS;
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
        return slot < stacks.length ? stacks[slot] : ItemStack.EMPTY;
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
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = ItemStack.EMPTY;
        }
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.is(ThItems.SYRINGE_INJECTION.get());
    }
}
