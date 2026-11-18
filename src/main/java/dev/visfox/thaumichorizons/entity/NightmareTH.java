package dev.visfox.thaumichorizons.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import dev.visfox.thaumichorizons.platform.NightmareTeleport;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class NightmareTH extends Endersteed {
    public NightmareTH(EntityType<? extends NightmareTH> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Endersteed.createAttributes();
    }

    /**
     * Low and mid charge jumps retain the Endersteed's short-range teleport.
     * A full charge while ridden in the Overworld or Nether moves the entire
     * vehicle/passenger tree to the other dimension.
     */
    @Override
    public void handleStartJump(int jumpScale) {
        double blocks = jumpScale / 7.0D;
        boolean travelDimension = jumpScale >= 90
            && (this.level().dimension() == Level.OVERWORLD || this.level().dimension() == Level.NETHER);
        if (!travelDimension) {
            this.enderTeleport(
                this.getX() - blocks * Math.sin(Math.toRadians(this.getYRot())),
                this.getY(),
                this.getZ() + blocks * Math.cos(Math.toRadians(this.getYRot())));
            return;
        }

        if (!(this.level() instanceof ServerLevel source)
            || !(this.getFirstPassenger() instanceof ServerPlayer)) {
            return;
        }

        ServerLevel destination = source.getServer().getLevel(
            source.dimension() == Level.OVERWORLD ? Level.NETHER : Level.OVERWORLD);
        if (destination == null) {
            return;
        }

        Vec3 arrival = findSafeArrival(destination);
        if (arrival == null) {
            return;
        }

        source.explode(this, this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(),
            2.0F, true, Level.ExplosionInteraction.MOB);

        // Entity.teleport carries passengers across dimensions and re-mounts
        // them on the replacement vehicle; no client-only mount packet needed.
        NightmareTeleport.transfer(this, destination, arrival);
    }

    private Vec3 findSafeArrival(ServerLevel destination) {
        int originX = Mth.floor(this.getX());
        int originZ = Mth.floor(this.getZ());
        for (int radius = 0; radius <= 16; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    Vec3 candidate = safeColumn(destination, originX + dx, originZ + dz);
                    if (candidate != null) {
                        return candidate;
                    }
                }
            }
        }

        // Do not relocate a Nightmare to an unrelated spawn point if no safe nearby landing exists.
        return null;
    }

    private Vec3 safeColumn(ServerLevel destination, int x, int z) {
        BlockPos surface = destination.getHeightmapPos(
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, destination.getMaxY() - 1, z));
        BlockPos ground = surface.below();
        if (!destination.getBlockState(ground).isFaceSturdy(destination, ground, Direction.UP)) {
            return null;
        }

        double targetX = x + 0.5D;
        double targetY = surface.getY();
        double targetZ = z + 0.5D;
        AABB arrivalBox = this.getBoundingBox().move(
            targetX - this.getX(), targetY - this.getY(), targetZ - this.getZ());
        if (!destination.noCollision(this, arrivalBox) || touchesLiquid(destination, arrivalBox)) {
            return null;
        }
        return new Vec3(targetX, targetY, targetZ);
    }

    private static boolean touchesLiquid(Level level, AABB box) {
        for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX); x++) {
            for (int y = Mth.floor(box.minY); y <= Mth.floor(box.maxY); y++) {
                for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ); z++) {
                    if (!level.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isScorchedPlant(BlockState state) {
        return state.is(BlockTags.LEAVES)
            || state.is(Blocks.COBWEB)
            || state.is(Blocks.VINE)
            || state.is(BlockTags.FLOWERS)
            || state.is(BlockTags.SAPLINGS)
            || state.is(BlockTags.CROPS)
            || state.getBlock() instanceof BushBlock;
    }

    private void scorch(ServerLevel level, BlockPos pos) {
        if (isScorchedPlant(level.getBlockState(pos))) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.sendParticles(ParticleTypes.FLAME,
                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                15, 0.25D, 0.25D, 0.25D, 0.02D);
        }
    }

    @Override
    public void tick() {
        Level level = this.level();
        boolean inLava = touchesLava(level, this.getBoundingBox());
        BlockPos below = BlockPos.containing(this.getX(), this.getY() - 1.0D, this.getZ());
        boolean standingOverLava = level.getFluidState(below).is(FluidTags.LAVA);
        Vec3 motion = this.getDeltaMovement();

        if (inLava) {
            this.setDeltaMovement(motion.x, Math.min(0.25D, motion.y + 0.1D), motion.z);
            this.setOnGround(true);
            this.fallDistance = 0.0F;
        } else if (standingOverLava) {
            this.setOnGround(true);
            this.fallDistance = 0.0F;
            if (motion.y < 0.0D) {
                this.setDeltaMovement(motion.x, 0.0D, motion.z);
            }
        }

        super.tick();

        if (this.level() instanceof ServerLevel serverLevel) {
            BlockPos foot = BlockPos.containing(this.getX(), this.getY() - 1.0D, this.getZ());
            if (serverLevel.getBlockState(foot).is(Blocks.GRASS_BLOCK)) {
                serverLevel.setBlockAndUpdate(foot, Blocks.DIRT.defaultBlockState());
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    foot.getX() + 0.5D, foot.getY() + 0.1D, foot.getZ() + 0.5D,
                    10, 0.3D, 0.05D, 0.3D, 0.015D);
            }

            BlockPos body = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            scorch(serverLevel, body);
            scorch(serverLevel, body.above());
        }
    }

    private static boolean touchesLava(Level level, AABB box) {
        for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX); x++) {
            for (int y = Mth.floor(box.minY); y <= Mth.floor(box.maxY); y++) {
                for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ); z++) {
                    if (level.getFluidState(new BlockPos(x, y, z)).is(FluidTags.LAVA)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 speedMultiplier) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, speedMultiplier);
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}
