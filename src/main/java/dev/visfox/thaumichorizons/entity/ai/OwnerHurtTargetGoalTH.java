package dev.visfox.thaumichorizons.entity.ai;

import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public class OwnerHurtTargetGoalTH extends TargetGoalTH {
    private final Mob tameableMob;
    @Nullable
    private LivingEntity target;
    private int timestamp;

    public OwnerHurtTargetGoalTH(Mob owner) {
        super(owner, false);
        this.tameableMob = owner;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    private @Nullable Player resolveOwner() {
        String ownerName = CreatureInfusions.get(this.tameableMob).owner();
        if (ownerName == null || ownerName.isEmpty()) {
            return null;
        }
        return this.tameableMob.level() instanceof ServerLevel level
            ? level.getServer().getPlayerList().getPlayerByName(ownerName)
            : null;
    }

    @Override
    public boolean canUse() {
        Player owner = resolveOwner();
        if (owner == null) {
            return false;
        }
        this.target = owner.getLastHurtByMob();
        int lastHurtByMobTimestamp = owner.getLastHurtByMobTimestamp();
        return lastHurtByMobTimestamp != this.timestamp && this.isSuitableTarget(this.target, false);
    }

    @Override
    public void start() {
        this.taskOwner.setTarget(this.target);
        Player owner = resolveOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}
