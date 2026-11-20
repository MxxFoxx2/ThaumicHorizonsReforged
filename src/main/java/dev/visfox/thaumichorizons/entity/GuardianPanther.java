package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;

import dev.visfox.thaumichorizons.entity.ai.AttackOnCollideGoalTH;
import dev.visfox.thaumichorizons.entity.ai.FollowOwnerGoalTH;
import dev.visfox.thaumichorizons.entity.ai.HurtByTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.OwnerHurtByTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.OwnerHurtTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.WanderGoalTH;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class GuardianPanther extends TamableAnimal {
    public GuardianPanther(EntityType<? extends GuardianPanther> type, Level level) {
        super(type, level);
        this.setTame(true, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.5F));
        this.goalSelector.addGoal(4, new AttackOnCollideGoalTH(this, 1.0, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoalTH(this, 1.0, 10.0F, 5.0F));
        this.goalSelector.addGoal(6, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(7, new WanderGoalTH(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoalTH(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoalTH(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoalTH(this, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
            .add(Attributes.MAX_HEALTH, 24.0)
            .add(Attributes.MOVEMENT_SPEED, 0.5)
            .add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null && this.getHealth() < this.getMaxHealth()) {
            stack.consume(1, player);
            this.heal(food.nutrition());
            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.mobInteract(player, hand);
    }

    public void resetStats() {
        var health = this.getAttribute(Attributes.MAX_HEALTH);
        var speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (health != null) {
            health.setBaseValue(24.0);
        }
        if (speed != null) {
            speed.setBaseValue(0.5);
        }
    }

    @Override
    public @Nullable GuardianPanther getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ThEntities.GUARDIAN_PANTHER.get().create(level, EntitySpawnReason.MOB_SUMMONED);
    }
}
