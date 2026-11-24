package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.item.InjectorItem;
import dev.visfox.thaumichorizons.item.SyringeKeys;
import dev.visfox.thaumichorizons.registry.ThItems;
import dev.visfox.thaumichorizons.registry.ThMenus;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;

public class InjectorMenu extends AbstractContainerMenu {
    public static final int AMMO_SLOTS = 7;
    private static final int[] SLOT_X = { 73, 99, 107, 92, 64, 45, 49 };
    private static final int[] SLOT_Y = { 10, 20, 47, 70, 72, 51, 24 };

    private final Player player;
    private final InjectorInventory ammoInv;
    private final ItemStack injector;
    private final int blockSlot;

    public InjectorMenu(int containerId, Inventory inventory) {
        super(ThMenus.INJECTOR.get(), containerId);
        this.player = inventory.player;
        this.ammoInv = new InjectorInventory();
        this.injector = inventory.getSelected();
        this.blockSlot = inventory.selected;
        List<ItemStack> ammo = new ArrayList<>();
        for (int i = 0; i < AMMO_SLOTS; i++) {
            ItemStack loaded = injector.getItem() instanceof InjectorItem injectorItem
                ? injectorItem.getAmmo(injector, i)
                : null;
            ammo.add(loaded == null ? ItemStack.EMPTY : loaded);
        }
        ammoInv.setItemRange(ammo);
        for (int i = 0; i < AMMO_SLOTS; i++) {
            addSlot(new RestrictedSlot(ammoInv, i, SLOT_X[i], SLOT_Y[i],
                stack -> stack.is(ThItems.SYRINGE_INJECTION.get())));
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int slotIndex = column + row * 9 + 9;
                addSlot(new Slot(inventory, slotIndex, 8 + column * 18, 108 + row * 18) {
                    @Override
                    public boolean mayPickup(Player picker) {
                        return slotIndex != blockSlot && super.mayPickup(picker);
                    }
                });
            }
        }
        for (int column = 0; column < 9; column++) {
            int slotIndex = column;
            addSlot(new Slot(inventory, slotIndex, 8 + column * 18, 166) {
                @Override
                public boolean mayPickup(Player picker) {
                    return slotIndex != blockSlot && super.mayPickup(picker);
                }
            });
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.copy();
            if (index < AMMO_SLOTS) {
                if (!moveItemStackTo(itemstack2, AMMO_SLOTS, AMMO_SLOTS + 36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack2, itemstack);
            } else {
                if (!itemstack2.is(ThItems.SYRINGE_INJECTION.get())
                    || !moveItemStackTo(itemstack2, 0, AMMO_SLOTS, false)) {
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

    @Override
    public void clicked(int slotIndex, int button, ClickType input, Player clicker) {
        if (slotIndex == blockSlot) {
            return;
        }
        super.clicked(slotIndex, button, input, clicker);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide() && injector.getItem() instanceof InjectorItem injectorItem) {
            List<ItemStack> ammo = new ArrayList<>();
            for (int i = 0; i < AMMO_SLOTS; i++) {
                ammo.add(ammoInv.getItem(i));
            }
            SyringeKeys.AMMO.set(injector, new SyringeKeys.AmmoData(ammo));
        }
    }

    public ItemStack injector() {
        return injector;
    }
}
