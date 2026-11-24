package dev.visfox.thaumichorizons.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.client.render.VatRenderAlphaContext;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Applies the legacy Vat clone opacity to the contained creature's living model. */
@Mixin(LivingEntityRenderer.class)
abstract class VatEntityAlphaMixin {
    @ModifyVariable(method = "getRenderType", at = @At("HEAD"), argsOnly = true, index = 2)
    private boolean thaumichorizons$forceTranslucentVatBody(boolean translucent) {
        return translucent || VatRenderAlphaContext.isActive();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target =
        "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private void thaumichorizons$applyVatAlpha(EntityModel<?> model, PoseStack poses, VertexConsumer buffer,
        int light, int overlay, int color) {
        if (VatRenderAlphaContext.isActive()) {
            int alpha = (int) (FastColor.ARGB32.alpha(color) * VatRenderAlphaContext.alpha());
            color = (color & 0x00FFFFFF) | (alpha << 24);
        }
        model.renderToBuffer(poses, buffer, light, overlay, color);
    }
}
