package dev.visfox.thaumichorizons.entity.ai;

import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public class SitGoalTH extends Goal {
    private final Mob entity;
    private boolean isSitting;

    public SitGoalTH(Mob entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.entity.isInWater()) {
            return false;
        }
        if (!this.entity.onGround()) {
            return false;
        }
        Player owner = resolveOwner();
        return owner == null || ((this.entity.distanceToSqr(owner) >= 144.0
            || owner.getLastHurtByMob() == null) && this.isSitting);
    }

    @Override
    public void start() {
        this.entity.getNavigation().stop();
        CreatureInfusions.setSitting(this.entity, true);
    }

    @Override
    public void stop() {
        CreatureInfusions.setSitting(this.entity, false);
    }

    public void setSitting(boolean sitting) {
        this.isSitting = sitting;
    }

    private @Nullable Player resolveOwner() {
        String ownerName = CreatureInfusions.get(this.entity).owner();
        if (ownerName == null || ownerName.isEmpty()) {
            return null;
        }
        return this.entity.level() instanceof net.minecraft.server.level.ServerLevel level
            ? level.getServer().getPlayerList().getPlayerByName(ownerName)
            : null;
    }
}
