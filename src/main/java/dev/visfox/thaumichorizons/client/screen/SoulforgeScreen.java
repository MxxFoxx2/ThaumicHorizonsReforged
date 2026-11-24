package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.SoulforgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SoulforgeScreen extends AbstractContainerScreen<SoulforgeMenu> {
    public SoulforgeScreen(SoulforgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 0;
        imageHeight = 0;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    }
}
