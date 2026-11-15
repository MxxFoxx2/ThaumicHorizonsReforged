package dev.visfox.thaumichorizons.menu;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.item.lenses.ILens;
import dev.visfox.thaumichorizons.item.lenses.ItemLensCase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ContainerCase extends AbstractContainerMenu {
    public static final int CASE_SLOTS = 18;
    public static final int INVENTORY_START = CASE_SLOTS;
    public static final int HOTBAR_START = INVENTORY_START + 27;
    public static final int SLOT_END = HOTBAR_START + 9;

    private final InventoryCase input = new InventoryCase(this);
    private final Inventory playerInventory;
    private final int caseSlotIndex;
    private final int blockedSlot;

    public int getBlockSlotIndex() {
        return caseSlotIndex;
    }

    public ContainerCase(int windowId, Inventory playerInventory, int selectedSlot) {
        super((MenuType<ContainerCase>) BuiltInRegistries.MENU.getValue(ThaumicHorizons.id("case")), windowId);
        this.playerInventory = playerInventory;
        this.caseSlotIndex = selectedSlot;
        this.blockedSlot = HOTBAR_START + selectedSlot;
        ItemStack pouch = playerInventory.getItem(selectedSlot);
        if (pouch.getItem() instanceof ItemLensCase) {
            ItemStack[] contents = ItemLensCase.getContents(pouch);
            for (int a = 0; a < CASE_SLOTS; a++) {
                this.input.setItem(a, contents[a]);
            }
        }
        for (int a = 0; a < CASE_SLOTS; a++) {
            addSlot(new LensSlot(this.input, a, 37 + a % 6 * 18, 51 + a / 6 * 18));
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 151 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 209));
        }
    }

    public int getBlockedSlot() {
        return blockedSlot;
    }

    private void save() {
        ItemStack pouch = this.playerInventory.getItem(this.caseSlotIndex);
        if (pouch.getItem() instanceof ItemLensCase) {
            ItemLensCase.setContents(pouch, this.input.getStackList());
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            save();
            this.playerInventory.setChanged();
        }
    }

    @Override
    public void clicked(int slotIndex, int buttonNum, ContainerInput containerInput, Player player) {
        if (slotIndex == blockedSlot) {
            return;
        }
        if (containerInput == ContainerInput.SWAP && buttonNum == blockedSlot - HOTBAR_START) {
            return;
        }
        super.clicked(slotIndex, buttonNum, containerInput, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index == blockedSlot) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        if (index < CASE_SLOTS && !(stack.getItem() instanceof ILens)) {
            return ItemStack.EMPTY;
        }
        ItemStack original = stack.copy();
        if (index < CASE_SLOTS) {
            if (!moveItemStackTo(stack, CASE_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, CASE_SLOTS, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().getItem(this.caseSlotIndex).getItem() instanceof ItemLensCase;
    }

    private static final class LensSlot extends Slot {
        private LensSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ILens;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
