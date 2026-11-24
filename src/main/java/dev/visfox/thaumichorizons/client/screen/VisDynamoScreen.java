package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.VisDynamoMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.aspect.Aspect;

public class VisDynamoScreen extends AbstractContainerScreen<VisDynamoMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("thaumichorizons", "textures/gui/guidynamo.png");

    public VisDynamoScreen(VisDynamoMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 111;
        imageHeight = 104;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        ThGui.drawTag(graphics, 11, 12, Aspect.AIR, 0.0F, !menu.tile().provideAer);
        ThGui.drawTag(graphics, 83, 11, Aspect.EARTH, 0.0F, !menu.tile().provideTerra);
        ThGui.drawTag(graphics, 11, 45, Aspect.FIRE, 0.0F, !menu.tile().provideIgnis);
        ThGui.drawTag(graphics, 83, 45, Aspect.WATER, 0.0F, !menu.tile().provideAqua);
        ThGui.drawTag(graphics, 11, 78, Aspect.ORDER, 0.0F, !menu.tile().provideOrdo);
        ThGui.drawTag(graphics, 83, 78, Aspect.ENTROPY, 0.0F, !menu.tile().providePerditio);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ThGui.sheet(graphics, TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0, 0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (int) mouseX - leftPos;
        int y = (int) mouseY - topPos;
        if (inRect(x, y, 11, 12)) {
            click(1);
            return true;
        }
        if (inRect(x, y, 83, 11)) {
            click(2);
            return true;
        }
        if (inRect(x, y, 11, 45)) {
            click(3);
            return true;
        }
        if (inRect(x, y, 83, 45)) {
            click(4);
            return true;
        }
        if (inRect(x, y, 11, 78)) {
            click(5);
            return true;
        }
        if (inRect(x, y, 83, 78)) {
            click(6);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean inRect(int x, int y, int rx, int ry) {
        return x >= rx && y >= ry && x <= rx + 16 && y <= ry + 16;
    }

    private void click(int button) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
        }
    }
}
