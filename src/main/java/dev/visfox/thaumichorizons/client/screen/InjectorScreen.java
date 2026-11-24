package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.InjectorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InjectorScreen extends AbstractContainerScreen<InjectorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("thaumichorizons", "textures/gui/guiinjector.png");

    public InjectorScreen(InjectorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 175;
        imageHeight = 191;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ThGui.sheet(graphics, TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0, 0);
    }
}
