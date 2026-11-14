package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.entity.ai.AttackOnCollideGoalTH;
import dev.visfox.thaumichorizons.entity.ai.FollowOwnerGoalTH;
import dev.visfox.thaumichorizons.entity.ai.HurtByTargetGoalTH;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Gravekeeper extends TamableAnimal {
    public Gravekeeper(EntityType<? extends Gravekeeper> type, Level level) {
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
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoalTH(this, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false,
            (target, lvl) -> target.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.animal.feline.Ocelot.createAttributes();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel level && this.tickCount % 1 == 0) {
            AABB box = new AABB(
                this.getX() - 5.0,
                this.getY() - 5.0,
                this.getZ() - 5.0,
                this.getX() + 5.0,
                this.getY() + 5.0,
                this.getZ() + 5.0);
            for (LivingEntity critter : level.getEntitiesOfClass(LivingEntity.class, box)) {
                if (critter.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD) && critter.isAlive()) {
                    critter.igniteForSeconds(1.0F);
                    if (Compat.port().fx().isAvailable()) {
                        Vec3 from = new Vec3(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ());
                        Vec3 to = new Vec3(critter.getX(), critter.getY() + critter.getBbHeight() / 2.0F, critter.getZ());
                        Compat.port().fx().wisp(level, from, to, 16773444, false);
                    }
                }
            }
        }
    }

    @Override
    public @Nullable Gravekeeper getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ThEntities.GRAVEKEEPER.get().create(level, EntitySpawnReason.MOB_SUMMONED);
    }
}
