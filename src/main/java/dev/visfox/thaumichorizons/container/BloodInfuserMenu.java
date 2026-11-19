package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.blockentity.BloodInfuserBlockEntity;
import dev.visfox.thaumichorizons.registry.ThItems;
import dev.visfox.thaumichorizons.registry.ThMenus;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.research.Knowledge;

public class BloodInfuserMenu extends AbstractContainerMenu {
    private static final int MAX_SELECTED_ASPECTS = 8;
    private final BloodInfuserBlockEntity tile;
    private final AspectList aspectsKnown;

    public BloodInfuserMenu(int containerId, Inventory inventory, BloodInfuserBlockEntity tile) {
        super(ThMenus.BLOOD_INFUSER.get(), containerId);
        this.tile = tile;
        this.aspectsKnown = Knowledge.get(inventory.player).aspects();
        addSlot(new RestrictedSlot(tile, 0, 16, 37, stack -> stack.is(ThItems.SYRINGE_BLOOD.get())));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                addSlot(new OutputSlot(tile, x * 3 + y + 1, 108 + x * 18, 19 + y * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 137 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 195));
        }
    }

    public BloodInfuserBlockEntity tile() {
        return tile;
    }

    public List<Aspect> aspectsKnown() {
        return aspectsKnown.getAspectsSorted();
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
            if (index < 10) {
                if (!moveItemStackTo(itemstack2, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack2, itemstack);
            } else {
                if (!itemstack2.is(ThItems.SYRINGE_BLOOD.get()) || !moveItemStackTo(itemstack2, 0, 1, false)) {
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

    private int selectedAspectCount() {
        int count = 0;
        for (Aspect aspect : tile.aspectsSelected.getAspects()) {
            count += Math.max(0, tile.aspectsSelected.getAmount(aspect));
        }
        return count;
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        List<Aspect> sorted = aspectsKnown.getAspectsSorted();
        if (button < 0) {
            button = -1 - button;
            if (button < sorted.size() && selectedAspectCount() < MAX_SELECTED_ASPECTS) {
                tile.aspectsSelected.add(sorted.get(button), 1);
                tile.sync();
                return true;
            }
            return false;
        }
        if (button > 2) {
            button -= 3;
            if (button < sorted.size() && tile.aspectsSelected.getAmount(sorted.get(button)) > 0) {
                tile.aspectsSelected.remove(sorted.get(button), 1);
                tile.sync();
                return true;
            }
            return false;
        }
        switch (button) {
            case 0 -> {
                tile.mode = 0;
                tile.sync();
                return true;
            }
            case 1 -> {
                tile.mode = 1;
                tile.sync();
                return true;
            }
            case 2 -> {
                tile.mode = 2;
                tile.sync();
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
