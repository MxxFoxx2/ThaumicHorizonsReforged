package dev.visfox.thaumichorizons.entity.ai;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.entity.TaintEaterPig;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import thaumcraft.taint.TaintBiomes;

public class EatTaintGoal extends Goal {
    private static final Set<ResourceLocation> TAINT_BLOCK_IDS = Set.of(
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "crusted_taint"),
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "tainted_soil"),
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "fibrous_taint"),
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "tainted_grass"),
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "tainted_plant"),
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "spore_stalk"),
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "mature_spore_stalk"),
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "flesh_block"));
    private static final Set<Block> TAINT_BLOCKS = new HashSet<>();

    private final TaintEaterPig thePig;
    private @Nullable BlockPos targetCoordinates;
    private int cooldown;
    private int count;

    public EatTaintGoal(TaintEaterPig pig) {
        this.thePig = pig;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    private static boolean isTaintBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (TAINT_BLOCKS.isEmpty()) {
            for (ResourceLocation id : TAINT_BLOCK_IDS) {
                BuiltInRegistries.BLOCK.getOptional(id).ifPresent(TAINT_BLOCKS::add);
            }
        }
        return TAINT_BLOCKS.contains(state.getBlock());
    }

    private static boolean isTaintBiome(Level level, BlockPos pos) {
        return TaintBiomes.isTainted(level, pos);
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        return this.findTaint();
    }

    private boolean findTaint() {
        BlockPos origin = this.thePig.blockPosition();
        for (int x = -2; x < 3; ++x) {
            for (int y = -2; y < 3; ++y) {
                for (int z = -2; z < 3; ++z) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (isTaintBlock(this.thePig.level(), pos)
                        || (this.thePig.level().getBlockState(pos).is(Blocks.GRASS_BLOCK)
                            && isTaintBiome(this.thePig.level(), pos))) {
                        this.targetCoordinates = pos;
                        return true;
                    }
                }
            }
        }
        for (int tries = 0; tries < 30; ++tries) {
            BlockPos pos = new BlockPos(
                origin.getX() + this.thePig.getRandom().nextInt(17) - 8,
                origin.getY() + this.thePig.getRandom().nextInt(5) - 2,
                origin.getZ() + this.thePig.getRandom().nextInt(17) - 8);
            if ((this.thePig.level().isEmptyBlock(pos.above()) && isTaintBlock(this.thePig.level(), pos))
                || (this.thePig.level().getBlockState(pos).is(Blocks.GRASS_BLOCK)
                    && isTaintBiome(this.thePig.level(), pos))) {
                this.targetCoordinates = pos;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.count-- > 0 && !this.thePig.getNavigation().isDone() && this.cooldown-- <= 0;
    }

    @Override
    public void stop() {
        this.count = 0;
        this.targetCoordinates = null;
        this.thePig.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetCoordinates == null) {
            return;
        }
        Vec3 center = Vec3.atCenterOf(this.targetCoordinates);
        this.thePig.getLookControl().setLookAt(center.x, center.y, center.z, 30.0F, 30.0F);
        double dist = this.thePig.distanceToSqr(center.x, center.y, center.z);
        if (dist <= 4.0) {
            this.eatTaint();
        }
    }

    private void eatTaint() {
        if (!(this.thePig.level() instanceof ServerLevel level) || this.targetCoordinates == null) {
            return;
        }
        BlockPos pos = this.targetCoordinates;
        if (isTaintBlock(level, pos)) {
            if (TcReforgedPort.fx.isAvailable()) {
                TcReforgedPort.fx.blocksplosion(level, pos, 0);
            }
            level.removeBlock(pos, false);
            TaintBiomes.set(level, pos, Biomes.PLAINS);
            level.playSound(null, this.thePig.getX(), this.thePig.getY(), this.thePig.getZ(),
                SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.2F,
                ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            this.thePig.heal(1.0F);
            this.cooldown = 20;
        } else if (level.getBlockState(pos).is(Blocks.GRASS_BLOCK) && isTaintBiome(level, pos)) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            TaintBiomes.set(level, pos, Biomes.PLAINS);
            level.playSound(null, this.thePig.getX(), this.thePig.getY(), this.thePig.getZ(),
                SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.2F,
                ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            this.thePig.heal(1.0F);
            this.cooldown = 10;
        } else {
            this.stop();
        }
    }

    @Override
    public void start() {
        this.count = 500;
        if (this.targetCoordinates != null) {
            Vec3 center = Vec3.atCenterOf(this.targetCoordinates);
            this.thePig.getNavigation().moveTo(center.x, center.y, center.z, 1.0);
        }
    }
}
