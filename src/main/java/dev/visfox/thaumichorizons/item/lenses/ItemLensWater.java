package dev.visfox.thaumichorizons.item.lenses;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ItemLensWater extends ItemLensBase {
    public ItemLensWater(Properties properties) {
        super(properties, "LensWater");
    }

    @Override
    public void handleRemoval(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
