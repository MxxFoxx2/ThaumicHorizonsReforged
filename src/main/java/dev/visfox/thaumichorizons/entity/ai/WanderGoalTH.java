package dev.visfox.thaumichorizons.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

public class WanderGoalTH extends Goal {
    private final Mob entity;
    private final double speed;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public WanderGoalTH(Mob entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.entity.getRandom().nextInt(120) != 0) {
            return false;
        }
        Vec3 target = RandomPositionGeneratorTH.findRandomTarget(this.entity, 10, 7);
        if (target == null) {
            return false;
        }
        this.xPosition = target.x;
        this.yPosition = target.y;
        this.zPosition = target.z;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.entity.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.entity.getNavigation().moveTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
}
