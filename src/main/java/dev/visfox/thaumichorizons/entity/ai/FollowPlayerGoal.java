package dev.visfox.thaumichorizons.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import javax.annotation.Nullable;
import thaumcraft.entity.golem.GolemBase;

public class FollowPlayerGoal extends Goal {
    private final GolemBase pet;
    private @Nullable LivingEntity owner;
    private final double moveSpeed;
    private final PathNavigation pathfinder;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;

    public FollowPlayerGoal(GolemBase pet, double moveSpeed, float minDist, float maxDist) {
        this.pet = pet;
        this.moveSpeed = moveSpeed;
        this.pathfinder = pet.getNavigation();
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = resolveOwner();
        if (owner == null) {
            return false;
        }
        if (this.pet.inactive) {
            return false;
        }
        if (this.pet.distanceToSqr(owner) < this.minDist * this.minDist) {
            return false;
        }
        this.owner = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity currentOwner = this.owner;
        return currentOwner != null
            && !this.pathfinder.isDone()
            && this.pet.distanceToSqr(currentOwner) > this.maxDist * this.maxDist
            && !this.pet.inactive;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.pathfinder.setCanFloat(true);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.pathfinder.stop();
        this.pathfinder.setCanFloat(false);
    }

    @Override
    public void tick() {
        LivingEntity currentOwner = this.owner;
        if (currentOwner == null) {
            return;
        }
        this.pet.getLookControl().setLookAt(currentOwner, 10.0F, this.pet.getMaxHeadXRot());
        if (!this.pet.inactive && --this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.pathfinder.moveTo(currentOwner, this.moveSpeed) && !this.pet.isLeashed()
                && this.pet.distanceToSqr(currentOwner) >= 144.0) {
                int x = currentOwner.getBlockX() - 2;
                int z = currentOwner.getBlockZ() - 2;
                int y = currentOwner.getBlockY();
                Level level = this.pet.level();
                for (int dx = 0; dx <= 4; ++dx) {
                    for (int dz = 0; dz <= 4; ++dz) {
                        if ((dx < 1 || dz < 1 || dx > 3 || dz > 3)
                            && hasSolidTopSurface(level, x + dx, y - 1, z + dz)
                            && !isNormalCube(level, x + dx, y, z + dz)
                            && !isNormalCube(level, x + dx, y + 1, z + dz)) {
                            this.pet.absMoveTo(x + dx + 0.5, y, z + dz + 0.5, this.pet.getYRot(), this.pet.getXRot());
                            this.pathfinder.stop();
                            return;
                        }
                    }
                }
            }
        }
    }

    private @Nullable LivingEntity resolveOwner() {
        java.util.UUID ownerUuid = this.pet.getOwnerUUID();
        if (ownerUuid == null) {
            return null;
        }
        return this.pet.level() instanceof ServerLevel level ? level.getPlayerByUUID(ownerUuid) : null;
    }

    private static boolean hasSolidTopSurface(Level level, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP);
    }

    private static boolean isNormalCube(Level level, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = level.getBlockState(pos);
        return state.isCollisionShapeFullBlock(level, pos);
    }
}
