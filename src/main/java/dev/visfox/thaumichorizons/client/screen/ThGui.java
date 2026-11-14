package dev.visfox.thaumichorizons.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public final class ThGui {
    private ThGui() {
    }

    public static void sheet(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height,
        int u, int v) {
        if (width > 0 && height > 0) {
            graphics.blit(texture, x, y, x + width, y + height, u / 256.0F, (u + width) / 256.0F, v / 256.0F,
                (v + height) / 256.0F);
        }
    }

    public static void drawTag(GuiGraphicsExtractor graphics, int x, int y, thaumcraft.aspect.Aspect aspect,
        float amount, boolean gray) {
        int color = gray ? 0xFF191919 : 0xFF000000 | aspect.getColor();
        dev.visfox.thaumichorizons.client.platform.GuiBlit.blit(graphics, aspect.getImage(), x, y, 16, 16, 0, 0, 32, 32,
            32, 32, color);
        if (amount > 0.0F) {
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            graphics.text(minecraft.font, format(amount), x, y, 0xFFFFFFFF, true);
        }
    }

    private static String format(float amount) {
        return amount == Math.floor(amount) ? String.valueOf((int) amount) : String.valueOf(amount);
    }
}
