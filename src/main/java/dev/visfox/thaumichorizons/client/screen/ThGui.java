package dev.visfox.thaumichorizons.client.screen;

import dev.visfox.thaumichorizons.client.platform.GuiBlit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class ThGui {
    private ThGui() {
    }

    public static void sheet(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height,
        int u, int v) {
        if (width > 0 && height > 0) {
            GuiBlit.blit(graphics, texture, x, y, width, height, u, v, width, height, 256, 256, 0xFFFFFFFF);
        }
    }

    public static void drawTag(GuiGraphics graphics, int x, int y, thaumcraft.aspect.Aspect aspect,
        float amount, boolean gray) {
        int color = gray ? 0xFF191919 : 0xFF000000 | aspect.getColor();
        GuiBlit.blit(graphics, aspect.getImage(), x, y, 16, 16, 0, 0, 32, 32,
            32, 32, color);
        if (amount > 0.0F) {
            graphics.drawString(Minecraft.getInstance().font, format(amount), x, y, 0xFFFFFFFF, true);
        }
    }

    private static String format(float amount) {
        return amount == Math.floor(amount) ? String.valueOf((int) amount) : String.valueOf(amount);
    }
}
