package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.SoulExtractorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SoulExtractorScreen extends AbstractContainerScreen<SoulExtractorMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("thaumichorizons", "textures/gui/guisieve.png");

    public SoulExtractorScreen(SoulExtractorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        ThGui.sheet(graphics, TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0, 0);
        if (menu.tile().isExtracting() || menu.tile().ticksLeft > 0) {
            int i1 = menu.tile().getTimeRemainingScaled(39);
            ThGui.sheet(graphics, TEXTURE, leftPos + 91, topPos + 58 - i1, 35, i1, 176, 166 - i1);
        }
    }
}
