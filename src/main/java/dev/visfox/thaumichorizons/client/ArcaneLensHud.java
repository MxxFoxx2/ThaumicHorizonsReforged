package dev.visfox.thaumichorizons.client;

import java.util.Map;
import java.util.TreeMap;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.item.lenses.ILens;
import dev.visfox.thaumichorizons.item.lenses.ItemLensCase;
import dev.visfox.thaumichorizons.item.lenses.LensManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class ArcaneLensHud {
    private static final TreeMap<String, ItemStack> LENSES = new TreeMap<>();
    private static boolean open;
    private static String hoveredLens;

    private ArcaneLensHud() {
    }

    public static boolean isWearingGoggles(Player player) {
        Item goggles = BuiltInRegistries.ITEM.getValue(
            Identifier.fromNamespaceAndPath("thaumcraft", "goggles_of_revealing"));
        return goggles != null && player.getItemBySlot(EquipmentSlot.HEAD).is(goggles);
    }

    public static boolean open(Player player) {
        LENSES.clear();
        hoveredLens = null;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            collectLens(player.getInventory().getItem(slot));
        }
        if (Compat.port().baubles().isAvailable()) {
            for (int slot = 0; slot < Compat.port().baubles().slotCount(); slot++) {
                collectLens(Compat.port().baubles().get(player, slot));
            }
        }
        open = !LENSES.isEmpty() && isWearingGoggles(player);
        return open;
    }

    private static void collectLens(ItemStack stack) {
        if (stack.getItem() instanceof ILens lens) {
            LENSES.put(lens.lensName(), stack.copy());
        } else if (stack.getItem() instanceof ItemLensCase) {
            for (ItemStack contained : ItemLensCase.getContents(stack)) {
                if (contained.getItem() instanceof ILens lens) {
                    LENSES.put(lens.lensName(), contained.copy());
                }
            }
        }
    }

    public static boolean isOpen() {
        return open;
    }

    public static String hoveredLens() {
        return hoveredLens;
    }

    public static void close() {
        open = false;
        hoveredLens = null;
        LENSES.clear();
    }

    @SubscribeEvent
    static void render(RenderGuiEvent.Post event) {
        if (!open) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.gui.screen() != null || !isWearingGoggles(player)) {
            close();
            return;
        }

        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        int centerX = graphics.guiWidth() / 2;
        int centerY = graphics.guiHeight() / 2;
        int mouseX = Mth.floor(minecraft.mouseHandler.getScaledXPos(minecraft.getWindow()));
        int mouseY = Mth.floor(minecraft.mouseHandler.getScaledYPos(minecraft.getWindow()));
        int count = LENSES.size();
        float radius = 18.0F + count * 2.5F;

        hoveredLens = null;
        for (int segment = 0; segment < 64; segment++) {
            double angle = Math.PI * 2.0 * segment / 64.0;
            int x = centerX + Mth.floor((float) Math.cos(angle) * radius);
            int y = centerY + Mth.floor((float) Math.sin(angle) * radius);
            graphics.fill(x - 2, y - 2, x + 2, y + 2, 0xB82C2637);
        }
        for (int segment = 0; segment < 48; segment++) {
            double angle = Math.PI * 2.0 * segment / 48.0;
            int x = centerX + Mth.floor((float) Math.cos(angle) * (radius - 5.0F));
            int y = centerY + Mth.floor((float) Math.sin(angle) * (radius - 5.0F));
            graphics.fill(x - 1, y - 1, x + 1, y + 1, 0x703E354B);
        }

        graphics.fill(centerX - 13, centerY - 13, centerX + 13, centerY + 13, 0xD51B1721);
        ItemStack currentLens = LensManager.getLensItem(
            LensManager.getLensId(player.getItemBySlot(EquipmentSlot.HEAD)));
        if (!currentLens.isEmpty()) {
            graphics.item(currentLens, centerX - 8, centerY - 8);
            if (Math.abs(mouseX - centerX) <= 12 && Math.abs(mouseY - centerY) <= 12) {
                graphics.setTooltipForNextFrame(minecraft.font, currentLens, mouseX, mouseY);
            }
        }

        int index = 0;
        for (Map.Entry<String, ItemStack> entry : LENSES.entrySet()) {
            double angle = -Math.PI / 2.0 + Math.PI * 2.0 * index / count;
            int x = centerX + Mth.floor((float) Math.cos(angle) * radius);
            int y = centerY + Mth.floor((float) Math.sin(angle) * radius);
            boolean hovered = Math.abs(mouseX - x) <= 10 && Math.abs(mouseY - y) <= 10;
            if (hovered) {
                hoveredLens = entry.getKey();
            }
            graphics.fill(
                x - 10,
                y - 10,
                x + 10,
                y + 10,
                hovered ? 0xE6B79B55 : 0xD52B2535);
            graphics.item(entry.getValue(), x - 8, y - 8);
            if (hovered) {
                graphics.setTooltipForNextFrame(minecraft.font, entry.getValue(), mouseX, mouseY);
            }
            index++;
        }

        String hint = "Release V to equip · Shift+V to remove";
        graphics.text(minecraft.font, hint, centerX - minecraft.font.width(hint) / 2,
            centerY + Mth.ceil(radius) + 18, 0xFFE8DDBF, true);
    }
}
