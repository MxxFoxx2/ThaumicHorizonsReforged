package dev.visfox.thaumichorizons.item.lenses;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ItemLensWater extends ItemLensBase {
    public ItemLensWater(Properties properties) {
        super(properties, "LensWater");
    }

    @Override
    public void handleRender(Player player, float partialTicks) {
        MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
        if (player.isInWater()
            && (effect == null || effect.getDuration() < 242)
            && System.currentTimeMillis() > LensManager.nightVisionOffTime) {
            LensManager.nightVisionOffTime = System.currentTimeMillis();
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 255, 0, true, false));
        } else if (!player.isInWater()) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    @Override
    public void handleRemoval(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
