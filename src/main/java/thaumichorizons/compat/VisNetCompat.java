package thaumichorizons.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface VisNetCompat {
    default boolean isAvailable() {
        return false;
    }

    default int drainVis(Level level, BlockPos pos, String aspectTag, int amount, int range) {
        throw new UnsupportedOperationException();
    }

    default int addVisToNode(Level level, BlockPos nodePos, String aspectTag, int amount) {
        throw new UnsupportedOperationException();
    }
}
