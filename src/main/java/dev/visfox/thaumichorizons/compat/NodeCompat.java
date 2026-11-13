package dev.visfox.thaumichorizons.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface NodeCompat {
    default boolean isAvailable() {
        return false;
    }

    default NodeInfo nodeAt(Level level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default boolean createNode(Level level, BlockPos pos, CompatAspectList aspects, NodeType type) {
        throw new UnsupportedOperationException();
    }

    default CompatAspectList consumeNode(Level level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    public enum NodeType {
        NORMAL,
        UNSTABLE,
        PALE,
        SINISTER,
        TAINTED,
        HUNGRY,
        PURE,
        ENERGIZED,
        SYNTHETIC,
        ETHEREAL
    }

    public record NodeInfo(CompatAspectList aspects, CompatAspectList base, NodeType type, String modifier) {
    }
}
