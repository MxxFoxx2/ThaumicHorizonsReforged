package thaumichorizons.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface EssentiaCompat {
    default boolean isAvailable() {
        return false;
    }

    default int drainEssentia(Level level, BlockPos from, String aspectTag, int amount, Direction side, int range) {
        throw new UnsupportedOperationException();
    }

    default boolean isTaintedMob(net.minecraft.world.entity.LivingEntity entity) {
        throw new UnsupportedOperationException();
    }
}
