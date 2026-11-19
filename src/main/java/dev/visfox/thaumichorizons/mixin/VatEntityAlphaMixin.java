package dev.visfox.thaumichorizons.mixin;

import dev.visfox.thaumichorizons.client.render.VatRenderAlphaContext;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Applies the legacy Vat clone opacity to the contained creature's living model. */
@Mixin(LivingEntityRenderer.class)
abstract class VatEntityAlphaMixin {
    @ModifyVariable(method = "getRenderType", at = @At("HEAD"), argsOnly = true, index = 3)
    private boolean thaumichorizons$forceTranslucentVatBody(boolean forceTransparent) {
        return forceTransparent || VatRenderAlphaContext.isActive();
    }

    @Inject(method = "getModelTint", at = @At("RETURN"), cancellable = true)
    private void thaumichorizons$applyVatAlpha(LivingEntityRenderState state,
        CallbackInfoReturnable<Integer> callback) {
        if (VatRenderAlphaContext.isActive()) {
            callback.setReturnValue(ARGB.multiplyAlpha(callback.getReturnValue(), VatRenderAlphaContext.alpha()));
        }
    }
}
