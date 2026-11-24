package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.InspiratronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InspiratronScreen extends AbstractContainerScreen<InspiratronMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("thaumichorizons", "textures/gui/guiinspiratron.png");

    public InspiratronScreen(InspiratronMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 219;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ThGui.sheet(graphics, TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0, 0);
        int i1 = menu.tile().getTimeRemainingScaled(28);
        ThGui.sheet(graphics, TEXTURE, leftPos + 66, topPos + 102, 44, i1, 176, 158 - i1);
    }
}
