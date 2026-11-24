package dev.visfox.thaumichorizons.entity.ai;

import dev.visfox.thaumichorizons.entity.OrePig;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nullable;

public class EatStoneGoal extends Goal {
    private final OrePig thePig;
    @Nullable
    private Entity targetEntity;
    private int count;

    public EatStoneGoal(OrePig pig) {
        this.thePig = pig;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.findItem();
    }

    private boolean findItem() {
        double minDistance = 128.0;
        AABB box = new AABB(
            this.thePig.getX() - 16.0,
            this.thePig.getY() - 16.0,
            this.thePig.getZ() - 16.0,
            this.thePig.getX() + 16.0,
            this.thePig.getY() + 16.0,
            this.thePig.getZ() + 16.0);
        for (Entity entity : this.thePig.level().getEntitiesOfClass(ItemEntity.class, box)) {
            ItemEntity item = (ItemEntity) entity;
            ItemStack stack = item.getItem();
            if (!stack.is(Items.COBBLESTONE)) {
                continue;
            }
            double distanceSq = item.distanceToSqr(this.thePig);
            if (distanceSq >= 256.0 || minDistance < distanceSq) {
                continue;
            }
            minDistance = distanceSq;
            this.targetEntity = item;
        }
        return this.targetEntity != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.count-- > 0 && !this.thePig.getNavigation().isDone() && this.targetEntity != null && this.targetEntity.isAlive();
    }

    @Override
    public void stop() {
        this.count = 0;
        this.targetEntity = null;
        this.thePig.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetEntity == null) {
            return;
        }
        this.thePig.getLookControl().setLookAt(this.targetEntity, 30.0F, 30.0F);
        double dist = this.thePig.distanceToSqr(this.targetEntity);
        if (dist <= 2.0) {
            this.pickUp();
        }
    }

    private void pickUp() {
        Level level = this.targetEntity.level();
        if (this.targetEntity instanceof ItemEntity item) {
            this.thePig.eatStone();
            ItemStack stack = item.getItem();
            stack.shrink(1);
            if (item.getItem().isEmpty()) {
                item.discard();
            }
        }
        level.playSound(null, this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(),
            SoundEvents.PLAYER_BURP, net.minecraft.sounds.SoundSource.NEUTRAL, 0.2F,
            ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
    }

    @Override
    public void start() {
        this.count = 500;
        if (this.targetEntity != null) {
            this.thePig.getNavigation().moveTo(this.targetEntity, this.thePig.getSpeed() + 1.0F);
        }
    }
}
