package dev.visfox.thaumichorizons.client.compat.tc.mixin;

import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.research.ResearchPage;

/** Draws the generic Void Putty repair rule on its dedicated research page. */
@Mixin(targets = "thaumcraft.client.research.ResearchPageScreen")
abstract class VoidPuttyResearchPageMixin {
    @Inject(method = "drawPage", at = @At("TAIL"))
    private void thaumichorizons$drawVoidPuttyPage(GuiGraphics graphics, @Coerce Object display,
        int side, int x, int y, CallbackInfo callback) {
        if (!(display instanceof DisplayPageAccessor accessor)
            || !(accessor.thaumichorizons$getSource() instanceof ResearchPage.Text text)
            || !"voidPutty2".equals(text.text())) {
            return;
        }

        int pageX = x - 15 + side * 152;
        int top = y + 68;
        ItemStack damaged = new ItemStack(Items.IRON_SWORD);
        damaged.setDamageValue(damaged.getMaxDamage() - 1);
        ItemStack putty = new ItemStack(ThItems.VOID_PUTTY.get());
        ItemStack repaired = new ItemStack(Items.IRON_SWORD);

        drawSlot(graphics, damaged, pageX + 4, top);
        graphics.drawCenteredString(Minecraft.getInstance().font, Component.literal("+"),
            pageX + 28, top + 5, 0xFF505050);
        drawSlot(graphics, putty, pageX + 36, top);
        graphics.drawCenteredString(Minecraft.getInstance().font, Component.literal(">"),
            pageX + 60, top + 5, 0xFF505050);
        drawSlot(graphics, repaired, pageX + 68, top);
    }

    private static void drawSlot(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF8B7A65);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF2B2118);
        graphics.renderItem(stack, x + 1, y + 1);
    }
}
