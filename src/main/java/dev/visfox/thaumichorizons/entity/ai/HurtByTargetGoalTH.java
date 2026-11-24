package dev.visfox.thaumichorizons.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import javax.annotation.Nullable;

public class HurtByTargetGoalTH extends TargetGoalTH {
    private final boolean alertOthers;
    private int timestamp;

    public HurtByTargetGoalTH(Mob owner, boolean alertOthers) {
        super(owner, false);
        this.alertOthers = alertOthers;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int lastHurtByMobTimestamp = this.taskOwner.getLastHurtByMobTimestamp();
        return lastHurtByMobTimestamp != this.timestamp && this.isSuitableTarget(this.taskOwner.getLastHurtByMob(), false);
    }

    @Override
    public void start() {
        LivingEntity lastHurtByMob = this.taskOwner.getLastHurtByMob();
        this.taskOwner.setTarget(lastHurtByMob);
        this.timestamp = this.taskOwner.getLastHurtByMobTimestamp();
        if (this.alertOthers) {
            double followDistance = this.getTargetDistance();
            List<? extends Mob> others = this.taskOwner.level().getEntitiesOfClass(
                this.taskOwner.getClass(),
                this.taskOwner.getBoundingBox().inflate(followDistance, 10.0, followDistance));
            for (Mob other : others) {
                if (this.taskOwner != other && other.getTarget() == null && !isAlliedToAttacker(other, lastHurtByMob)) {
                    other.setTarget(lastHurtByMob);
                }
            }
        }
        super.start();
    }

    private static boolean isAlliedToAttacker(Mob other, @Nullable LivingEntity attacker) {
        return attacker == null || other.isAlliedTo(attacker);
    }
}
