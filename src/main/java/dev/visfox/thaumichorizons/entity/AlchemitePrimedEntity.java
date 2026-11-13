package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.lib.ExplosionAlchemite;
import dev.visfox.thaumichorizons.registry.ThEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class AlchemitePrimedEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(AlchemitePrimedEntity.class,
        EntityDataSerializers.INT);
    private @Nullable LivingEntity alchemitePlacedBy;

    public AlchemitePrimedEntity(EntityType<? extends AlchemitePrimedEntity> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public AlchemitePrimedEntity(Level level, double x, double y, double z, @Nullable LivingEntity placer) {
        this(ThEntities.ALCHEMITE_PRIMED.get(), level);
        this.setPos(x, y, z);
        double rot = Math.random() * Math.PI * 2.0;
        this.setDeltaMovement(-Math.sin(rot) * 0.02, 0.20000000298023224, -Math.cos(rot) * 0.02);
        this.setFuse(80);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.alchemitePlacedBy = placer;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_FUSE_ID, 80);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }
        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (fuse <= 0) {
            this.discard();
            if (!this.level().isClientSide()) {
                this.explode();
            }
        } else if (this.level().isClientSide()) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    private void explode() {
        ExplosionAlchemite explosion = new ExplosionAlchemite(this.level(), this.alchemitePlacedBy,
            this.getX(), this.getY(), this.getZ(), 5.0F);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
    }

    @Override
    public boolean hurtServer(ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) {
        return false;
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    public void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE_ID, fuse);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Fuse", this.getFuse());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.setFuse(input.getIntOr("Fuse", 80));
    }
}
