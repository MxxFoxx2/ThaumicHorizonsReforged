package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.FingersMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class FingersScreen extends AbstractContainerScreen<FingersMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("thaumichorizons", "textures/gui/guifingers.png");

    public FingersScreen(FingersMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 190, 234);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        ThGui.sheet(graphics, TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0, 0);
    }
}
