package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.registry.ThMenus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.Nullable;
import thaumcraft.crafting.ArcaneCrafting;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.Wands;

public class FingersMenu extends AbstractContainerMenu {
    public static final int RESULT_SLOT = 0;
    public static final int WAND_SLOT = 1;
    public static final int GRID_START = 2;
    public static final int INVENTORY_START = 11;
    public static final int HOTBAR_START = 38;
    public static final int SLOT_END = 47;

    private final Player player;
    private final FingersInventory inventory;
    private final ResultContainer resultContainer = new ResultContainer();
    private List<ItemStack> cachedGrid = List.of();

    public FingersMenu(int containerId, Inventory playerInventory) {
        super(ThMenus.FINGERS.get(), containerId);
        this.player = playerInventory.player;
        this.inventory = new FingersInventory();
        addSlot(new FingersResultSlot(player, inventory, resultContainer, RESULT_SLOT, 160, 64));
        addSlot(new Slot(inventory, WAND_SLOT, 160, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return Wands.isWand(stack) && !Wands.isStaff(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                addSlot(new Slot(inventory, column + row * 3, 40 + column * 24, 40 + row * 24));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 16 + column * 18, 151 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 16 + column * 18, 209));
        }
        if (player instanceof ServerPlayer) {
            resultContainer.setItem(RESULT_SLOT, computeResult());
        }
    }

    public Container craftMatrix() {
        return inventory;
    }

    public ItemStack getWand() {
        return inventory.getItem(WAND_SLOT);
    }

    private List<ItemStack> gridStacks() {
        List<ItemStack> grid = new ArrayList<>(FingersInventory.GRID_SLOTS);
        for (int index = 0; index < FingersInventory.GRID_SLOTS; index++) {
            grid.add(inventory.getItem(index));
        }
        return grid;
    }

    private ItemStack computeResult() {
        if (!(player.level() instanceof ServerLevel level) || !(player instanceof ServerPlayer serverPlayer)) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> grid = gridStacks();
        CraftingInput input = CraftingInput.ofPositioned(ArcaneCrafting.GRID_SIZE, ArcaneCrafting.GRID_SIZE, grid).input();
        Optional<RecipeHolder<CraftingRecipe>> recipe = level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, level);
        ItemStack result = ItemStack.EMPTY;
        if (recipe.isPresent()) {
            RecipeHolder<CraftingRecipe> holder = recipe.get();
            if (resultContainer.setRecipeUsed(serverPlayer, holder)) {
                ItemStack assembled = holder.value().assemble(input);
                if (assembled.isItemEnabled(level.enabledFeatures())) {
                    result = assembled;
                }
            }
        }
        if (result.isEmpty()) {
            ItemStack wand = getWand();
            if (wand.getItem() instanceof WandItem wandItem) {
                ArcaneCrafting.Match match = ArcaneCrafting.find(grid, player);
                if (match != null && wandItem.consumeAllVisCrafting(wand, player, match.aspects(), false)) {
                    result = match.result();
                }
            }
        }
        return result;
    }

    public void onCraftMatrixChanged() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ItemStack result = computeResult();
        resultContainer.setItem(RESULT_SLOT, result);
        setRemoteSlot(RESULT_SLOT, result);
        broadcastChanges();
    }

    @Override
    public void slotsChanged(Container container) {
        onCraftMatrixChanged();
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
            if (index == RESULT_SLOT) {
                if (!moveItemStackTo(itemstack2, INVENTORY_START, SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack2, itemstack);
            } else if (index >= INVENTORY_START && index < HOTBAR_START) {
                if (Wands.isWand(itemstack2) && !Wands.isStaff(itemstack2)) {
                    if (!moveItemStackTo(itemstack2, WAND_SLOT, WAND_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(itemstack2, HOTBAR_START, SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= HOTBAR_START && index < SLOT_END) {
                if (Wands.isWand(itemstack2) && !Wands.isStaff(itemstack2)) {
                    if (!moveItemStackTo(itemstack2, WAND_SLOT, WAND_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(itemstack2, INVENTORY_START, HOTBAR_START, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack2, INVENTORY_START, SLOT_END, false)) {
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            for (int i = 0; i < FingersInventory.SLOT_COUNT; i++) {
                if (i == RESULT_SLOT) {
                    continue;
                }
                ItemStack stack = inventory.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {
                    player.drop(stack, false, net.minecraft.util.Prediction.PREDICTED);
                }
            }
        }
    }

    private final class FingersResultSlot extends Slot {
        private final Player player;
        private final Container craftMatrix;

        private FingersResultSlot(Player player, Container craftMatrix, Container result, int slot, int x, int y) {
            super(result, slot, x, y);
            this.player = player;
            this.craftMatrix = craftMatrix;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player taker, ItemStack stack) {
            for (int i = 0; i < FingersInventory.GRID_SLOTS; i++) {
                ItemStack grid = craftMatrix.getItem(i);
                if (!grid.isEmpty()) {
                    grid.shrink(1);
                    if (grid.isEmpty()) {
                        craftMatrix.setItem(i, ItemStack.EMPTY);
                    }
                }
            }
            ItemStack wand = craftMatrix.getItem(WAND_SLOT);
            if (wand.getItem() instanceof WandItem wandItem) {
                List<ItemStack> grid = new ArrayList<>();
                for (int i = 0; i < FingersInventory.GRID_SLOTS; i++) {
                    grid.add(craftMatrix.getItem(i));
                }
                ArcaneCrafting.Match match = ArcaneCrafting.find(grid, player);
                if (match != null) {
                    wandItem.consumeAllVisCrafting(wand, player, match.aspects(), true);
                }
            }
            onCraftMatrixChanged();
            super.onTake(taker, stack);
        }
    }
}
