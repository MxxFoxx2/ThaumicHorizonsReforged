package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.blockentity.SoulforgeBlockEntity;
import dev.visfox.thaumichorizons.registry.ThMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class SoulforgeMenu extends AbstractContainerMenu {
    private final SoulforgeBlockEntity tile;

    public SoulforgeMenu(int containerId, Inventory inventory, SoulforgeBlockEntity tile) {
        super(ThMenus.SOULFORGE.get(), containerId);
        this.tile = tile;
    }

    @Override
    public boolean stillValid(Player player) {
        return tile.isStillUsableByPlayer(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
