package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.SoulExtractorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SoulExtractorScreen extends AbstractContainerScreen<SoulExtractorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("thaumichorizons", "textures/gui/guisieve.png");

    public SoulExtractorScreen(SoulExtractorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ThGui.sheet(graphics, TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0, 0);
        if (menu.tile().isExtracting() || menu.tile().ticksLeft > 0) {
            int i1 = menu.tile().getTimeRemainingScaled(39);
            ThGui.sheet(graphics, TEXTURE, leftPos + 91, topPos + 58 - i1, 35, i1, 176, 166 - i1);
        }
    }
}
