package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.menu.ContainerCase;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiCase extends AbstractContainerScreen<ContainerCase> {
    private final int blockSlot;

    public GuiCase(ContainerCase menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 195, 175);
        this.blockSlot = menu.getBlockSlotIndex();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (minecraft.player != null && minecraft.player.getInventory().getItem(blockSlot).isEmpty()) {
            onClose();
        }
    }

}
