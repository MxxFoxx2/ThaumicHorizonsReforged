package dev.visfox.thaumichorizons.compat.tc.mixin;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.lib.TcBlocks;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.research.ResearchPage;

/** Draws the 5x5 gateway construction layout on the dedicated Pocket Plane research page. */
@Mixin(targets = "thaumcraft.client.research.ResearchPageScreen")
abstract class PlanarGatewayResearchPageMixin {
    private static final Identifier PAGE_MARKER =
        ThaumicHorizons.id("textures/misc/planar_gateway_page.png");

    @Inject(method = "drawPage", at = @At("TAIL"))
    private void thaumichorizons$drawGatewayPage(GuiGraphicsExtractor graphics, @Coerce Object display,
        int side, int x, int y, CallbackInfo callback) {
        if (!(display instanceof DisplayPageAccessor accessor)
            || !(accessor.thaumichorizons$getSource() instanceof ResearchPage.Image image)
            || !PAGE_MARKER.equals(image.image())) {
            return;
        }

        int pageX = x - 15 + side * 152;
        graphics.centeredText(Minecraft.getInstance().font, Component.translatable(image.caption()),
            pageX + 69, y + 9, 0xFF505050);

        int cellSize = 18;
        int left = pageX + 24;
        int top = y + 25;
        Block cornerBlock = TcBlocks.block("advanced_alchemical_construct");
        Block edgeBlock = TcBlocks.block("ancient_stone");
        ItemStack corner = stack(cornerBlock);
        ItemStack edge = stack(edgeBlock);
        ItemStack slot = new ItemStack(PocketRegistry.SLOT_ITEM.get());

        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 5; column++) {
                int px = left + column * cellSize;
                int py = top + row * cellSize;
                boolean isCorner = (row == 0 || row == 4) && (column == 0 || column == 4);
                boolean isSlot = row == 0 && column == 2;
                boolean isEdge = !isCorner && !isSlot
                    && (row == 0 || row == 4 || column == 0 || column == 4);

                graphics.fill(px, py, px + cellSize, py + cellSize,
                    isSlot ? 0xFF6A527C : isCorner ? 0xFFB5A477 : isEdge ? 0xFFAAA69A : 0xFF597C89);
                graphics.outline(px, py, cellSize, cellSize, 0xFF4B463D);

                ItemStack icon = isSlot ? slot : isCorner ? corner : isEdge ? edge : ItemStack.EMPTY;
                if (!icon.isEmpty()) {
                    graphics.item(icon, px + 1, py + 1);
                } else if (!isCorner && !isSlot && !isEdge) {
                    graphics.fill(px + 4, py + 4, px + cellSize - 4, py + cellSize - 4, 0xFF8FBAC8);
                } else {
                    String symbol = isSlot ? "S" : isCorner ? "A" : "E";
                    graphics.centeredText(Minecraft.getInstance().font, symbol, px + cellSize / 2,
                        py + 5, 0xFF29251F);
                }
            }
        }

        graphics.centeredText(Minecraft.getInstance().font,
            Component.translatable("thaumichorizons.research.pocketPlane.gateway.aperture"),
            pageX + 69, top + 97, 0xFF505050);
        graphics.centeredText(Minecraft.getInstance().font,
            Component.translatable("thaumichorizons.research.pocketPlane.gateway.legend"),
            pageX + 69, top + 109, 0xFF777777);
    }

    private static ItemStack stack(Block block) {
        if (block == null || block.asItem() == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(block.asItem());
    }
}
