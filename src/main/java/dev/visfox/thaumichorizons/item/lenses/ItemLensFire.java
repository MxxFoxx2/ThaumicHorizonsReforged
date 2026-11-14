package dev.visfox.thaumichorizons.item.lenses;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ItemLensFire extends ItemLensBase {
    public ItemLensFire(Properties properties) {
        super(properties, "LensFire");
    }

    @Override
    public void handleRemoval(Player player) {
        MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
        if (LensManager.isEffectGrantedByLens(effect)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
