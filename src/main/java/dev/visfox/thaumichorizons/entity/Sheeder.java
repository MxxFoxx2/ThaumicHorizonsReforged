package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.entity.ai.AttackOnCollideGoalTH;
import dev.visfox.thaumichorizons.entity.ai.HurtByTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.WanderGoalTH;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Sheeder extends Spider implements Shearable {
    private static final EntityDataAccessor<Byte> DATA_SHEARED = SynchedEntityData.defineId(Sheeder.class, EntityDataSerializers.BYTE);
    private static final byte SHEARED_FLAG = 16;
    private int sheepTimer;
    private final EatBlockGoal grazeGoal;

    public Sheeder(EntityType<? extends Sheeder> type, Level level) {
        super(type, level);
        this.grazeGoal = new EatBlockGoal(this);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 16.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.5));
        this.goalSelector.addGoal(2, new AttackOnCollideGoalTH(this, 1.0, false));
        this.goalSelector.addGoal(3, new TemptGoal(this, 0.44, stack -> stack.is(Items.WHEAT), false));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, this.grazeGoal);
        this.goalSelector.addGoal(6, new WanderGoalTH(this, 0.4));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoalTH(this, false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_SHEARED, (byte) 0);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        this.sheepTimer = this.grazeGoal.getEatAnimationTick();
        super.customServerAiStep(level);
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide()) {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }
        super.aiStep();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 10) {
            this.sheepTimer = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    public boolean readyForShearing() {
        return !this.isSheared() && !this.isBaby() && this.isAlive();
    }

    @Override
    public void shear(ServerLevel level, SoundSource soundSource, ItemStack tool) {
        level.playSound(null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
        int count = 2 + this.random.nextInt(4);
        for (int index = 0; index < count; ++index) {
            this.spawnAtLocation(level, new ItemStack(Items.STRING), 1.0F);
        }
        this.setSheared(true);
    }

    public void setSheared(boolean sheared) {
        byte flags = this.entityData.get(DATA_SHEARED);
        if (sheared) {
            flags = (byte) (flags | SHEARED_FLAG);
        } else {
            flags = (byte) (flags & ~SHEARED_FLAG);
        }
        this.entityData.set(DATA_SHEARED, flags);
    }

    public boolean isSheared() {
        return (this.entityData.get(DATA_SHEARED) & SHEARED_FLAG) != 0;
    }

    @Override
    public void ate() {
        this.setSheared(false);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setSheared(input.getBooleanOr("Sheared", false));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Sheared", this.isSheared());
    }

    @Override
    protected void dropFromLootTable(ServerLevel level, DamageSource source, boolean hitByPlayer) {
        if (!this.isSheared()) {
            this.spawnAtLocation(level, new ItemStack(Items.STRING), 0.0F);
        }
    }
}
