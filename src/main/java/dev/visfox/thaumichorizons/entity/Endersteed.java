package dev.visfox.thaumichorizons.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class Endersteed extends Horse {
    private static final net.minecraft.resources.ResourceLocation SPEED_MODIFIER_ID =
        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("thaumichorizons", "endersteed_speed");
    private static final net.minecraft.resources.ResourceLocation JUMP_MODIFIER_ID =
        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("thaumichorizons", "endersteed_jump");
    private static final net.minecraft.resources.ResourceLocation HEALTH_MODIFIER_ID =
        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("thaumichorizons", "endersteed_health");

    private boolean initialized;

    public Endersteed(EntityType<? extends Endersteed> type, Level level) {
        super(type, level);
        this.initialized = false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.initialized = tag.getBoolean("initialized");
        if (!this.initialized) {
            this.getAttribute(Attributes.MOVEMENT_SPEED)
                .addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_ID, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            this.getAttribute(Attributes.JUMP_STRENGTH)
                .addPermanentModifier(new AttributeModifier(JUMP_MODIFIER_ID, 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            this.getAttribute(Attributes.MAX_HEALTH)
                .addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER_ID, 4.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            this.initialized = true;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("initialized", this.initialized);
    }

    @Override
    public void handleStartJump(int jumpScale) {
        double blocks = jumpScale / 7.0;
        this.enderTeleport(
            this.getX() - blocks * Math.sin(Math.toRadians(this.getYRot())),
            this.getY(),
            this.getZ() + blocks * Math.cos(Math.toRadians(this.getYRot())));
    }

    private static boolean touchesLiquid(Level level, AABB box) {
        for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX); ++x) {
            for (int y = Mth.floor(box.minY); y <= Mth.floor(box.maxY); ++y) {
                for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ); ++z) {
                    if (!level.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean enderTeleport(double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(this, x, y, z);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }
        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();
        Level level = this.level();
        this.setPos(event.getTargetX(), Math.floor(event.getTargetY()) - 3, event.getTargetZ());
        boolean teleported = false;
        boolean foundGround = false;
        boolean foundAir = false;
        int blockX = Mth.floor(this.getX());
        int blockZ = Mth.floor(this.getZ());
        int blockY = Mth.floor(this.getY());
        while ((!foundGround || !foundAir) && blockY < oldY + 4.0) {
            if (level.getBlockState(new BlockPos(blockX, blockY - 1, blockZ)).isSolid()) {
                foundGround = true;
            } else if (!foundGround) {
                ++blockY;
                this.setPos(this.getX(), blockY, this.getZ());
            }
            if (foundGround) {
                this.setPos(this.getX(), blockY, this.getZ());
                AABB box = this.getBoundingBox();
                if (level.noCollision(this, box) && !touchesLiquid(level, box)) {
                    teleported = true;
                    foundAir = true;
                }
            }
            ++blockY;
            this.setPos(this.getX(), blockY, this.getZ());
        }
        this.setPos(this.getX(), this.getY(), this.getZ());
        if (!teleported) {
            this.setPos(oldX, oldY, oldZ);
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            for (int particle = 0; particle < 128; ++particle) {
                double progress = particle / 127.0;
                double px = oldX + (this.getX() - oldX) * progress + (this.random.nextDouble() - 0.5) * this.getBbWidth() * 2.0;
                double py = oldY + (this.getY() - oldY) * progress + this.random.nextDouble() * this.getBbHeight();
                double pz = oldZ + (this.getZ() - oldZ) * progress + (this.random.nextDouble() - 0.5) * this.getBbWidth() * 2.0;
                serverLevel.sendParticles(ParticleTypes.PORTAL, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
        level.playSound(null, oldX, oldY, oldZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        return true;
    }
}
