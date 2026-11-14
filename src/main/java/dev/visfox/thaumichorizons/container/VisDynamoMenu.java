package dev.visfox.thaumichorizons.container;

import dev.visfox.thaumichorizons.blockentity.VisDynamoBlockEntity;
import dev.visfox.thaumichorizons.registry.ThMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class VisDynamoMenu extends AbstractContainerMenu {
    private final VisDynamoBlockEntity tile;

    public VisDynamoMenu(int containerId, Inventory inventory, VisDynamoBlockEntity tile) {
        super(ThMenus.VIS_DYNAMO.get(), containerId);
        this.tile = tile;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5,
            tile.getBlockPos().getZ() + 0.5) <= 64.0;
    }

    public VisDynamoBlockEntity tile() {
        return tile;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        switch (button) {
            case 1 -> {
                tile.provideAer = !tile.provideAer;
                tile.setChanged();
                return true;
            }
            case 2 -> {
                tile.provideTerra = !tile.provideTerra;
                tile.setChanged();
                return true;
            }
            case 3 -> {
                tile.provideIgnis = !tile.provideIgnis;
                tile.setChanged();
                return true;
            }
            case 4 -> {
                tile.provideAqua = !tile.provideAqua;
                tile.setChanged();
                return true;
            }
            case 5 -> {
                tile.provideOrdo = !tile.provideOrdo;
                tile.setChanged();
                return true;
            }
            case 6 -> {
                tile.providePerditio = !tile.providePerditio;
                tile.setChanged();
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
