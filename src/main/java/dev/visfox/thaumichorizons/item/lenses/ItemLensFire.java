package dev.visfox.thaumichorizons.item.lenses;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ItemLensFire extends ItemLensBase {
    public ItemLensFire(Properties properties) {
        super(properties, "LensFire");
    }

    @Override
    public void handleRender(Player player, float partialTicks) {
        MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
        if (!player.isInWater()) {
            if ((effect == null || (LensManager.isEffectGrantedByLens(effect) && effect.getDuration() < 242))
                && System.currentTimeMillis() > LensManager.nightVisionOffTime) {
                LensManager.nightVisionOffTime = System.currentTimeMillis();
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 255, -1, true, false));
            }
        } else if (LensManager.isEffectGrantedByLens(effect)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    @Override
    public void handleRemoval(Player player) {
        MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
        if (LensManager.isEffectGrantedByLens(effect)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
