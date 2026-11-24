package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.container.VatMenu;
import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import javax.annotation.Nullable;

public class VatScreen extends AbstractContainerScreen<VatMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("thaumichorizons", "textures/gui/guivat.png");

    public VatScreen(VatMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 209;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ThGui.sheet(graphics, TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0, 0);
        if (!TcReforgedPort.research.isComplete(menu.player(), "incarnationVat")) {
            ThGui.sheet(graphics, TEXTURE, leftPos + 58, topPos + 30, 57, 20, 176, 163);
        }
        LivingEntity critter = menu.tile().getEntity();
        if (critter != null) {
            drawHealth(graphics, critter.getHealth() / 2.0F, critter.getMaxHealth() / 2.0F);
        } else if (menu.tile().mode == 4 || menu.tile().mode == 2) {
            drawHealth(graphics, menu.tile().selfInfusionHealth / 2.0F, 10.0F);
        }
        int[] infusions = null;
        int row = 209;
        if (critter != null) {
            infusions = CreatureInfusions.get(critter).infusions();
            row = 209;
        } else if (menu.tile().mode == 4 || menu.tile().selfInfusions[0] != 0) {
            infusions = menu.tile().selfInfusions;
            row = 225;
        }
        if (infusions != null) {
            for (int k = 0; k < 12; k++) {
                if (infusions[k] == 0) {
                    break;
                }
                ThGui.sheet(graphics, TEXTURE, leftPos + 55 + 16 * (k % 4), topPos + 56 + 17 * (k / 4), 16, 16,
                    (infusions[k] - 1) * 16, row);
            }
        }
    }

    private void drawHealth(GuiGraphics graphics, float adjustedHealth, float max) {
        for (int i = 0; i < (int) max; i++) {
            int x = leftPos + 56 + 7 * i - 63 * (i / 9);
            int y = topPos + 12 + 7 * (i / 9);
            if (adjustedHealth >= i) {
                ThGui.sheet(graphics, TEXTURE, x, y, 7, 6, 176, 126);
            } else {
                ThGui.sheet(graphics, TEXTURE, x, y, 7, 6, 176, 120);
                if (adjustedHealth >= i - 0.5F) {
                    ThGui.sheet(graphics, TEXTURE, x, y, 4, 6, 176, 126);
                }
            }
        }
    }
}
