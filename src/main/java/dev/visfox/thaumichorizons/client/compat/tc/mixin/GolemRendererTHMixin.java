package dev.visfox.thaumichorizons.client.compat.tc.mixin;

import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.client.render.entity.golem.GolemRenderer;
import thaumcraft.client.render.entity.golem.GolemRenderState;
import thaumcraft.entity.golem.GolemBase;
import thaumcraft.item.golem.GolemItems;

/** Restores TH-specific golem presentation without requiring the fork-only renderer hook. */
@Mixin(value = GolemRenderer.class, remap = false)
public abstract class GolemRendererTHMixin {
    @Shadow @Final private thaumcraft.client.platform.ItemModelResolver itemResolver;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void thaumichorizons$applyMaterialPresentation(
        GolemBase entity, GolemRenderState state, float partialTicks, CallbackInfo ci
    ) {
        if (!(entity instanceof EntityGolemTH golem)) {
            return;
        }

        state.texture = golem.getGolemTexture();

        int materialSlots = Math.min(
            state.upgrades.length,
            golem.material().upgrades + (golem.isAdvanced() ? 1 : 0)
        );
        for (int slot = state.upgradeCount; slot < materialSlots; slot++) {
            int upgrade = golem.getUpgrade(slot);
            if (upgrade >= 0) {
                itemResolver.updateForLiving(
                    state.upgrades[slot], GolemItems.upgrade(upgrade), ItemDisplayContext.NONE, entity
                );
            } else {
                state.upgrades[slot].clear();
            }
        }
        state.upgradeCount = materialSlots;
    }
}
