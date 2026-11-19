package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.BrainJarCompat;
import java.util.function.DoubleSupplier;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class BrainJarFeed {
    private static final double CHANCE = 0.99;

    private BrainJarFeed() {
    }

    public static boolean feed(BlockEntity entity, BrainJarCompat jars, int bits, DoubleSupplier random) {
        if (!jars.isBrainJar(entity)) {
            return false;
        }
        int max = jars.maxXp();
        int current = jars.xp(entity);
        if (max <= 0 || current >= max) {
            return false;
        }
        int next = xpAfter(current, max, bits, random);
        if (next != current) {
            jars.setXp(entity, next);
        }
        return true;
    }

    public static int xpAfter(int currentXp, int maxXp, int bits, DoubleSupplier random) {
        int xp = Math.min(currentXp, maxXp);
        for (int i = 0; i < bits && xp < maxXp; i++) {
            if (random.getAsDouble() > CHANCE) {
                xp++;
            }
        }
        return xp;
    }
}
