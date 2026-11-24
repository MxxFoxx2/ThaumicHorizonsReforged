package dev.visfox.thaumichorizons.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

public class LostSoul extends Mob implements Enemy {
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;

    private static @Nullable SoundEvent tcSound(String path, @Nullable SoundEvent fallback) {
        return net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT
            .getOptional(ResourceLocation.fromNamespaceAndPath("thaumcraft", path))
            .orElse(fallback);
    }

    public LostSoul(EntityType<LostSoul> type, Level level) {
        super(type, level);
        this.courseChangeCooldown = 0;
        this.xpReward = 5;
        this.setNoGravity(true);
        this.moveControl = new net.minecraft.world.entity.ai.control.FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 1.0)
            .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        net.minecraft.world.entity.ai.navigation.FlyingPathNavigation navigation =
            new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    public void travel(Vec3 input) {
        if (this.isControlledByLocalInstance()) {
            this.moveRelative(0.02F, input);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
        }
        this.calculateEntityAnimation(false);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void checkFallDamage(double ya, boolean onGround, BlockState onState, BlockPos pos) {
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected int decreaseAirSupply(int currentSupply) {
        return currentSupply;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        ServerLevel level = (ServerLevel) this.level();
        double dx = this.waypointX - this.getX();
        double dy = this.waypointY - this.getY();
        double dz = this.waypointZ - this.getZ();
        double distance = dx * dx + dy * dy + dz * dz;
        if (distance < 1.0 || distance > 3600.0) {
            this.waypointX = this.getX() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0;
            this.waypointY = this.getY() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0;
            this.waypointZ = this.getZ() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0;
        }
        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown += this.random.nextInt(5) + 2;
            distance = Math.sqrt(distance);
            if (this.isCourseTraversable(level, distance)) {
                this.setDeltaMovement(this.getDeltaMovement().add(dx / distance * 0.1, dy / distance * 0.1, dz / distance * 0.1));
            } else {
                this.waypointX = this.getX();
                this.waypointY = this.getY();
                this.waypointZ = this.getZ();
            }
        }
        Vec3 motion = this.getDeltaMovement();
        float yaw = -((float) Math.atan2(motion.x, motion.z)) * 180.0F / (float) Math.PI;
        this.setYRot(yaw);
        this.yBodyRot = yaw;
    }

    private boolean isCourseTraversable(ServerLevel level, double distance) {
        AABB box = this.getBoundingBox();
        double stepX = (this.waypointX - this.getX()) / distance;
        double stepY = (this.waypointY - this.getY()) / distance;
        double stepZ = (this.waypointZ - this.getZ()) / distance;
        for (int step = 1; step < distance; ++step) {
            box = box.move(stepX, stepY, stepZ);
            if (!level.noCollision(this, box)) {
                return false;
            }
        }
        BlockPos waypoint = BlockPos.containing(this.waypointX, this.waypointY, this.waypointZ);
        if (level.getBlockState(waypoint).liquid()) {
            return false;
        }
        for (int below = 0; below < 11; ++below) {
            if (!level.getBlockState(waypoint.below(below)).isAir()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return tcSound("wisplive", super.getAmbientSound());
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FIRE_EXTINGUISH;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return tcSound("wispdead", super.getDeathSound());
    }

    @Override
    protected float getSoundVolume() {
        return 0.25F;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean checkSpawnRules(net.minecraft.world.level.LevelAccessor level, net.minecraft.world.entity.MobSpawnType reason) {
        return true;
    }
}
