package dev.visfox.thaumichorizons.entity.ai;

import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;

public class OwnerHurtByTargetGoalTH extends TargetGoalTH {
    private final Mob defendingMob;
    @Nullable
    private LivingEntity ownerAttacker;
    private int timestamp;

    public OwnerHurtByTargetGoalTH(Mob owner) {
        super(owner, false);
        this.defendingMob = owner;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        Player owner = resolveOwner();
        if (owner == null) {
            return false;
        }
        this.ownerAttacker = owner.getLastHurtByMob();
        int lastHurtByMobTimestamp = owner.getLastHurtByMobTimestamp();
        return lastHurtByMobTimestamp != this.timestamp && this.isSuitableTarget(this.ownerAttacker, false);
    }

    @Override
    public void start() {
        this.taskOwner.setTarget(this.ownerAttacker);
        Player owner = resolveOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }
        super.start();
    }

    protected final @Nullable Player resolveOwner() {
        String ownerName = CreatureInfusions.get(this.defendingMob).owner();
        if (ownerName == null || ownerName.isEmpty()) {
            return null;
        }
        return this.defendingMob.level() instanceof ServerLevel level
            ? level.getServer().getPlayerList().getPlayerByName(ownerName)
            : null;
    }
}
