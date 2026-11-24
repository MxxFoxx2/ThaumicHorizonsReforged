package dev.visfox.thaumichorizons.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

public final class RandomPositionGeneratorTH {
    private RandomPositionGeneratorTH() {
    }

    public static @Nullable Vec3 findRandomTarget(Mob mob, int rangeX, int rangeY) {
        return findRandomTargetBlock(mob, rangeX, rangeY, null);
    }

    public static @Nullable Vec3 findRandomTargetBlockTowards(Mob mob, int rangeX, int rangeY, Vec3 towards) {
        Vec3 direction = new Vec3(towards.x - mob.getX(), towards.y - mob.getY(), towards.z - mob.getZ());
        return findRandomTargetBlock(mob, rangeX, rangeY, direction);
    }

    public static @Nullable Vec3 findRandomTargetBlockAwayFrom(Mob mob, int rangeX, int rangeY, Vec3 awayFrom) {
        Vec3 direction = new Vec3(mob.getX() - awayFrom.x, mob.getY() - awayFrom.y, mob.getZ() - awayFrom.z);
        return findRandomTargetBlock(mob, rangeX, rangeY, direction);
    }

    private static @Nullable Vec3 findRandomTargetBlock(Mob mob, int rangeX, int rangeY, @Nullable Vec3 direction) {
        net.minecraft.util.RandomSource random = mob.getRandom();
        boolean found = false;
        int x = 0;
        int y = 0;
        int z = 0;
        for (int attempt = 0; attempt < 10; ++attempt) {
            int tryX = random.nextInt(2 * rangeX) - rangeX;
            int tryY = random.nextInt(2 * rangeY) - rangeY;
            int tryZ = random.nextInt(2 * rangeX) - rangeX;
            if (direction == null || tryX * direction.x + tryZ * direction.z >= 0.0) {
                x = tryX + Mth.floor(mob.getX());
                y = tryY + Mth.floor(mob.getY());
                z = tryZ + Mth.floor(mob.getZ());
                found = true;
            }
        }
        if (found) {
            return new Vec3(x, y, z);
        }
        return null;
    }
}
