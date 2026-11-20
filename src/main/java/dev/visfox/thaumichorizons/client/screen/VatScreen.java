package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.container.VatMenu;
import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.Nullable;

public class VatScreen extends AbstractContainerScreen<VatMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("thaumichorizons", "textures/gui/guivat.png");

    public VatScreen(VatMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 209);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
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

    private void drawHealth(GuiGraphicsExtractor graphics, float adjustedHealth, float max) {
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
