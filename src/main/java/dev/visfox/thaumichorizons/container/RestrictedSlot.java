package dev.visfox.thaumichorizons.container;

import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RestrictedSlot extends Slot {
    private final Predicate<ItemStack> allowed;

    public RestrictedSlot(Container container, int slot, int x, int y, Predicate<ItemStack> allowed) {
        super(container, slot, x, y);
        this.allowed = allowed;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return allowed.test(stack);
    }
}
