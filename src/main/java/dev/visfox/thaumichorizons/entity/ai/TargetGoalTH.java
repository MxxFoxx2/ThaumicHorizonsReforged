package dev.visfox.thaumichorizons.entity.ai;

import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jspecify.annotations.Nullable;

public abstract class TargetGoalTH extends Goal {
    protected final Mob taskOwner;
    protected boolean shouldCheckSight;
    private final boolean nearbyOnly;
    private int targetSearchStatus;
    private int targetSearchDelay;
    private int unseenMemoryTicks;

    protected TargetGoalTH(Mob owner, boolean checkSight) {
        this(owner, checkSight, false);
    }

    protected TargetGoalTH(Mob owner, boolean checkSight, boolean nearbyOnly) {
        this.taskOwner = owner;
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = nearbyOnly;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.taskOwner.getTarget();
        if (target == null) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        double followDistance = this.getTargetDistance();
        if (this.taskOwner.distanceToSqr(target) > followDistance * followDistance) {
            return false;
        }
        if (this.shouldCheckSight) {
            if (this.taskOwner.getSensing().hasLineOfSight(target)) {
                this.unseenMemoryTicks = 0;
            } else if (++this.unseenMemoryTicks > 60) {
                return false;
            }
        }
        return !(target instanceof Player player && player.getAbilities().instabuild);
    }

    protected double getTargetDistance() {
        return this.taskOwner.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    @Override
    public void start() {
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.unseenMemoryTicks = 0;
    }

    @Override
    public void stop() {
        this.taskOwner.setTarget(null);
    }

    protected boolean isSuitableTarget(@Nullable LivingEntity target, boolean attackCreativePlayers) {
        if (target == null) {
            return false;
        }
        if (target == this.taskOwner) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (this.taskOwner instanceof OwnableEntity owner && owner.getOwnerReference() != null) {
            if (target instanceof OwnableEntity other && owner.getOwnerReference().equals(other.getOwnerReference())) {
                return false;
            }
            LivingEntity ownerEntity = owner.getOwner();
            if (target == ownerEntity) {
                return false;
            }
        } else if (sameCreatureOwner(target)) {
            return false;
        }
        if (target instanceof Player player && !attackCreativePlayers && player.getAbilities().instabuild) {
            return false;
        }
        if (this.shouldCheckSight && !this.taskOwner.getSensing().hasLineOfSight(target)) {
            return false;
        }
        if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
                this.targetSearchStatus = 0;
            }
            if (this.targetSearchStatus == 0) {
                this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
            }
            return this.targetSearchStatus != 2;
        }
        return true;
    }

    private boolean sameCreatureOwner(LivingEntity target) {
        String owner = CreatureInfusions.get(this.taskOwner).owner();
        if (owner == null || owner.isEmpty()) {
            return false;
        }
        String targetOwner = CreatureInfusions.get(target).owner();
        return targetOwner != null && targetOwner.equals(owner);
    }

    private boolean canEasilyReach(LivingEntity target) {
        this.targetSearchDelay = 10 + this.taskOwner.getRandom().nextInt(5);
        Path path = this.taskOwner.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        }
        Node end = path.getEndNode();
        if (end == null) {
            return false;
        }
        int dx = end.x - Mth.floor(target.getX());
        int dz = end.z - Mth.floor(target.getZ());
        return dx * dx + dz * dz <= 2.25;
    }
}
