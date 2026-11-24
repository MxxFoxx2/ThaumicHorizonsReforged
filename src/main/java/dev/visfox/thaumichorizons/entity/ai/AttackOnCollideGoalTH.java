package dev.visfox.thaumichorizons.entity.ai;

import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import javax.annotation.Nullable;

public class AttackOnCollideGoalTH extends Goal {
    private final Mob attacker;
    private final double speedTowardsTarget;
    private final boolean longMemory;
    @Nullable
    private final Class<?> targetClass;
    @Nullable
    private Path path;
    private int cooldown;
    private int attackTick;
    private double lastTargetX;
    private double lastTargetY;
    private double lastTargetZ;
    private int failedPathFindingPenalty;

    public AttackOnCollideGoalTH(Mob attacker, double speed, boolean longMemory) {
        this(attacker, null, speed, longMemory);
    }

    public AttackOnCollideGoalTH(Mob attacker, @Nullable Class<?> targetClass, double speed, boolean longMemory) {
        this.attacker = attacker;
        this.targetClass = targetClass;
        this.speedTowardsTarget = speed;
        this.longMemory = longMemory;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.attacker.getTarget();
        if (target == null) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass())) {
            return false;
        }
        if (--this.cooldown <= 0) {
            this.path = this.attacker.getNavigation().createPath(target, 0);
            this.cooldown = 4 + this.attacker.getRandom().nextInt(7);
            return this.path != null;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.attacker.getTarget();
        return target != null && target.isAlive()
            && (this.longMemory || !this.attacker.getNavigation().isDone());
    }

    @Override
    public void start() {
        this.attacker.getNavigation().moveTo(this.path, this.speedTowardsTarget);
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        this.attacker.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.attacker.getTarget();
        if (target == null) {
            return;
        }
        this.attacker.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distanceSq = this.attacker.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
        double reach = this.attacker.getBbWidth() * 2.0F * this.attacker.getBbWidth() * 2.0F + target.getBbWidth();
        --this.cooldown;
        if ((this.longMemory || this.attacker.getSensing().hasLineOfSight(target)) && this.cooldown <= 0
            && ((this.lastTargetX == 0.0 && this.lastTargetY == 0.0 && this.lastTargetZ == 0.0)
                || target.distanceToSqr(this.lastTargetX, this.lastTargetY, this.lastTargetZ) >= 1.0
                || this.attacker.getRandom().nextFloat() < 0.05F)) {
            this.lastTargetX = target.getX();
            this.lastTargetY = target.getBoundingBox().minY;
            this.lastTargetZ = target.getZ();
            this.cooldown = this.failedPathFindingPenalty + 4 + this.attacker.getRandom().nextInt(7);
            Path currentPath = this.attacker.getNavigation().getPath();
            if (currentPath != null) {
                Node end = currentPath.getEndNode();
                if (end != null && target.distanceToSqr(end.x, end.y, end.z) < 1.0) {
                    this.failedPathFindingPenalty = 0;
                } else {
                    this.failedPathFindingPenalty += 10;
                }
            } else {
                this.failedPathFindingPenalty += 10;
            }
            if (distanceSq > 1024.0) {
                this.cooldown += 10;
            } else if (distanceSq > 256.0) {
                this.cooldown += 5;
            }
            if (!this.attacker.getNavigation().moveTo(target, this.speedTowardsTarget)) {
                this.cooldown += 15;
            }
        }
        this.attackTick = Math.max(this.attackTick - 1, 0);
        if (distanceSq <= reach && this.attackTick <= 20) {
            this.attackTick = 20;
            if (!this.attacker.getMainHandItem().isEmpty()) {
                this.attacker.swing(InteractionHand.MAIN_HAND, true);
            }
            float damage = 3.0F;
            if (this.attacker.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                damage += (float) this.attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
            }
            if (this.attacker.level() instanceof ServerLevel level) {
                target.hurt(this.attacker.damageSources().mobAttack(this.attacker), damage);
            }
        }
    }
}
