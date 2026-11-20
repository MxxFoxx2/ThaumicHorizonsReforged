package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.registry.ThEntities;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import dev.visfox.thaumichorizons.entity.ai.AttackOnCollideGoalTH;
import dev.visfox.thaumichorizons.entity.ai.FollowOwnerGoalTH;
import dev.visfox.thaumichorizons.entity.ai.HurtByTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.OwnerHurtByTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.OwnerHurtTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.WanderGoalTH;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.Wands;

public class Familiar extends TamableAnimal {
    public Familiar(EntityType<? extends Familiar> type, Level level) {
        super(type, level);
        this.setTame(true, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new AttackOnCollideGoalTH(this, 1.0, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoalTH(this, 1.0, 10.0F, 5.0F));
        this.goalSelector.addGoal(6, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(7, new WanderGoalTH(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoalTH(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoalTH(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoalTH(this, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Ocelot.createAttributes();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            var fx = TcReforgedPort.fx;
            if (fx.isAvailable() && this.getRandom().nextFloat() > 0.97F) {
                float angle = this.getRandom().nextFloat() * (float) (Math.PI * 2.0);
                fx.sparkle(
                    this.level(),
                    (float) this.getX() + this.getBbWidth() * (float) Math.cos(angle),
                    (float) this.getY() + this.getBbHeight() * (this.getRandom().nextFloat() - 0.1F) * 1.2F,
                    (float) this.getZ() + this.getBbWidth() * (float) Math.sin(angle),
                    2.0F,
                    0,
                    0.0F
                );
            }
        }
        if (!(this.level() instanceof ServerLevel level) || this.tickCount % 10 != 0) {
            return;
        }
        List<Player> players = level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(5.0));
        for (Player player : players) {
            ItemStack held = player.getMainHandItem();
            if (Wands.isWand(held)) {
                AspectList room = Wands.getAspectsWithRoom(held);
                for (Aspect aspect : room.getAspects()) {
                    Wands.storeVis(held, aspect, Wands.getVis(held, aspect) + 1);
                }
            }
        }
    }

    @Override
    public @Nullable Familiar getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ThEntities.FAMILIAR.get().create(level, EntitySpawnReason.MOB_SUMMONED);
    }
}
