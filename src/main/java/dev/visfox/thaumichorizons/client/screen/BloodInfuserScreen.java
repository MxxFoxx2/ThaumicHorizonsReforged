package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.container.BloodInfuserMenu;
import dev.visfox.thaumichorizons.blockentity.BloodInfuserBlockEntity;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;

public class BloodInfuserScreen extends AbstractContainerScreen<BloodInfuserMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("thaumichorizons",
        "textures/gui/guibloodinfuser.png");
    private static final int MAX_SELECTED = 8;

    private final Aspect[] aspectsSelected = new Aspect[MAX_SELECTED];
    private int numSelected;
    private int offset;
    private List<Aspect> aspectsKnown;

    public BloodInfuserScreen(BloodInfuserMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 219;
        BloodInfuserBlockEntity tile = menu.tile();
        for (Aspect asp : tile.aspectsSelected.getAspects()) {
            if (asp != null) {
                for (int i = 0; i < tile.aspectsSelected.getAmount(asp) && numSelected < MAX_SELECTED; i++) {
                    aspectsSelected[numSelected] = asp;
                    numSelected++;
                }
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        List<Aspect> known = knownAspects();
        AspectList alreadyUsed = new AspectList();
        for (int i = 0; i < numSelected; i++) {
            Aspect asp = aspectsSelected[i];
            if (asp == null) {
                break;
            }
            alreadyUsed.add(asp, 1);
            boolean knownHere = isKnown(asp, known);
            ThGui.drawTag(graphics, 54 + ((i % 2 == 1) ? 17 : 0), 12 + 17 * (i / 2), asp, 0.0F,
                !knownHere || menu.tile().aspectsAcquired.getAmount(asp) < alreadyUsed.getAmount(asp));
        }
        drawAspectList(graphics, known);
        ThGui.sheet(graphics, TEXTURE, leftPos + 111, topPos + 75, 24, 8, canScrollLeft() ? 177 : 177, canScrollLeft() ? 136 : 144);
        ThGui.sheet(graphics, TEXTURE, leftPos + 135, topPos + 75, 24, 8, 201, canScrollRight(known) ? 136 : 144);
        if (menu.tile().mode == 1) {
            ThGui.sheet(graphics, TEXTURE, leftPos + 38, topPos + 57, 6, 6, 178, 130);
        } else if (menu.tile().mode == 2) {
            ThGui.sheet(graphics, TEXTURE, leftPos + 38, topPos + 70, 6, 6, 178, 130);
        }
        Aspect mousedOver = mousedAspect(mouseX, mouseY, known);
        if (mousedOver != null) {
            HashMap<net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>, Integer> effects =
                menu.tile().getEffects(mousedOver);
            if (effects != null) {
                int j = 0;
                int k = 0;
                for (java.util.Map.Entry<net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>, Integer> entry
                    : effects.entrySet()) {
                    graphics.drawString(font, Component.literal(entry.getKey().value().getDescriptionId() + " "
                        + entry.getValue()), leftPos + 171, topPos + 72 + k * 10, 0xFF808080, false);
                    k++;
                }
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ThGui.sheet(graphics, TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0, 0);
    }

    private List<Aspect> knownAspects() {
        return menu.aspectsKnown();
    }

    private boolean isKnown(Aspect asp, List<Aspect> known) {
        return known.contains(asp);
    }

    private boolean canScrollLeft() {
        return offset > 0;
    }

    private boolean canScrollRight(List<Aspect> known) {
        return offset + 16 < known.size();
    }

    private void drawAspectList(GuiGraphics graphics, List<Aspect> known) {
        for (int i = 0; i < 16 && offset + i < known.size(); i++) {
            Aspect asp = known.get(offset + i);
            ThGui.drawTag(graphics, 14 + 18 * (i / 2), 83 + ((i % 2 == 0) ? 18 : 0), asp, 0.0F, false);
        }
    }

    private @Nullable Aspect mousedAspect(int mouseX, int mouseY, List<Aspect> known) {
        int x = mouseX - (leftPos + 14);
        int y = mouseY - (topPos + 83);
        for (int i = 0; i < 16; i++) {
            int cellX = x - 18 * (i / 2);
            int cellY = y - ((i % 2 == 0) ? 18 : 0);
            if (cellX >= 0 && cellX <= 16 && cellY >= 0 && cellY <= 16 && offset + i < known.size()) {
                return known.get(offset + i);
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<Aspect> known = knownAspects();
        int x = (int) mouseX - leftPos;
        int y = (int) mouseY - topPos;
        if (inRect(x, y, 38, 57, 6, 6)) {
            click(menu.tile().mode == 1 ? 0 : 1);
            return true;
        }
        if (inRect(x, y, 38, 70, 6, 6)) {
            click(menu.tile().mode == 2 ? 0 : 2);
            return true;
        }
        if (canScrollLeft() && inRect(x, y, 111, 75, 24, 8)) {
            offset -= 2;
            return true;
        }
        if (canScrollRight(known) && inRect(x, y, 135, 75, 24, 8)) {
            offset += 2;
            return true;
        }
        for (int i = 0; i < 16; i++) {
            int ax = 14 + 18 * (i / 2);
            int ay = 83 + ((i % 2 == 0) ? 18 : 0);
            if (offset + i < known.size() && numSelected < MAX_SELECTED && inRect(x, y, ax, ay, 16, 16)) {
                Aspect asp = known.get(offset + i);
                aspectsSelected[numSelected] = asp;
                numSelected++;
                menu.tile().aspectsSelected.add(asp, 1);
                click(-1 * (offset + i) - 1);
                return true;
            }
        }
        for (int i = 0; i < MAX_SELECTED; i++) {
            int sx = 54 + ((i % 2 == 1) ? 17 : 0);
            int sy = 12 + 17 * (i / 2);
            if (numSelected > i && aspectsSelected[i] != null && inRect(x, y, sx, sy, 16, 16)) {
                Aspect removed = aspectsSelected[i];
                menu.tile().aspectsSelected.remove(removed, 1);
                int index = known.indexOf(removed);
                click(3 + Math.max(index, 0));
                for (int j = i; j < MAX_SELECTED - 1; j++) {
                    aspectsSelected[j] = aspectsSelected[j + 1];
                }
                aspectsSelected[MAX_SELECTED - 1] = null;
                numSelected--;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean inRect(int x, int y, int rx, int ry, int w, int h) {
        return x >= rx && y >= ry && x <= rx + w && y <= ry + h;
    }

    private void click(int button) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
        }
    }
}
