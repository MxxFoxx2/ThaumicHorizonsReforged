package thaumichorizons.compat;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public interface PotionCompat {
    default boolean isAvailable() {
        return false;
    }

    default Holder<MobEffect> visExhaust() {
        throw new UnsupportedOperationException();
    }

    default Holder<MobEffect> thaumarhia() {
        throw new UnsupportedOperationException();
    }
}
