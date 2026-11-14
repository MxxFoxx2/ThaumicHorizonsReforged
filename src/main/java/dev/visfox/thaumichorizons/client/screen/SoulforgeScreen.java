package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.SoulforgeMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SoulforgeScreen extends AbstractContainerScreen<SoulforgeMenu> {
    public SoulforgeScreen(SoulforgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 0, 0);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
    }
}
