package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.item.SyringeKeys;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SampleSlot extends Slot {
    public SampleSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack what) {
        return what.is(ThItems.SYRINGE_BLOOD_SAMPLE.get()) || what.is(ThItems.CORPSE_EFFIGY.get())
            || what.is(Items.CHICKEN) || what.is(Items.BEEF) || what.is(Items.PORKCHOP);
    }
}
