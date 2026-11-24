package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.menu.ContainerCase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiCase extends AbstractContainerScreen<ContainerCase> {
    private final int blockSlot;

    public GuiCase(ContainerCase menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 195;
        imageHeight = 175;
        this.blockSlot = menu.getBlockSlotIndex();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (minecraft.player != null && minecraft.player.getInventory().getItem(blockSlot).isEmpty()) {
            onClose();
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    }
}
