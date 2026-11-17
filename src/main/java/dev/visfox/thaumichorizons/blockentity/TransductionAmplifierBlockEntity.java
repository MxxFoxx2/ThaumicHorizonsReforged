package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.block.TransductionAmplifierBlock;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.node.EnergizedNodeBlockEntity;
import thaumcraft.node.Node;

public class TransductionAmplifierBlockEntity extends ThBlockEntity {
    public int count = -1;
    public boolean activated;
    public boolean shouldActivate;
    boolean lastActivated;
    boolean fireOnce;

    public TransductionAmplifierBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.TRANSDUCTION_AMPLIFIER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TransductionAmplifierBlockEntity entity) {
        entity.tick();
    }

    private void tick() {
        if (!fireOnce) {
            fireOnce = true;
        }
        if (activated) {
            count++;
        } else if (!activated && count > 0) {
            if (count > 50) {
                count = 50;
            }
            count--;
        }
        Direction dir = getBlockState().hasProperty(TransductionAmplifierBlock.FACING)
            ? getBlockState().getValue(TransductionAmplifierBlock.FACING)
            : Direction.UP;
        BlockPos nodePos = worldPosition.relative(dir.getOpposite());
        if (shouldActivate && !activated) {
            if (level != null && level.getBlockEntity(nodePos) instanceof EnergizedNodeBlockEntity node) {
                boostNode(node);
            } else if (level != null && !level.isClientSide()) {
                level.destroyBlock(worldPosition, true);
                return;
            }
            activated = true;
            sync();
        } else if (!shouldActivate && activated) {
            if (level != null && level.getBlockEntity(nodePos) instanceof EnergizedNodeBlockEntity node) {
                unboostNode(node);
            } else if (level != null && !level.isClientSide()) {
                level.destroyBlock(worldPosition, true);
                return;
            }
            activated = false;
            sync();
        }
        if (level != null && !level.isClientSide() && activated && count % 10 == 0) {
            int ecks = worldPosition.getX();
            int why = worldPosition.getY();
            int zee = worldPosition.getZ();
            if (dir == Direction.NORTH) {
                zee++;
            } else if (dir == Direction.SOUTH) {
                zee--;
            } else if (dir == Direction.WEST) {
                ecks++;
            } else if (dir == Direction.EAST) {
                ecks--;
            }
            int transducers = 0;
            if (isActivatedTransducer(ecks + 1, why, zee)) {
                transducers++;
            }
            if (isActivatedTransducer(ecks - 1, why, zee)) {
                transducers++;
            }
            if (isActivatedTransducer(ecks, why, zee + 1)) {
                transducers++;
            }
            if (isActivatedTransducer(ecks, why, zee - 1)) {
                transducers++;
            }
            BlockPos targetNodePos = new BlockPos(ecks, why, zee);
            if (transducers > 3 && count % 50 == 0 && level instanceof ServerLevel serverLevel
                    && level.getBlockEntity(targetNodePos) instanceof EnergizedNodeBlockEntity node) {
                unboostNode(node);
                AspectList aspects = node.getAuraBase().copy();
                if (Compat.port().nodes().isAvailable()) {
                    Compat.port().nodes().stopTransducerForVortexConversion(serverLevel, targetNodePos.above());
                }
                serverLevel.setBlock(targetNodePos, ThBlocks.VORTEX.get().defaultBlockState(), 3);
                if (serverLevel.getBlockEntity(targetNodePos) instanceof VortexBlockEntity vortex) {
                    vortex.aspects = aspects;
                    vortex.sync();
                }
            }
            if (transducers > 2 && count % 50 == 0 && level instanceof ServerLevel serverLevel
                    && serverLevel.getRandom().nextInt(4) == 2 && Compat.port().flux().isAvailable()) {
                int dx = serverLevel.getRandom().nextInt(16) - 8;
                int dy = serverLevel.getRandom().nextInt(16) - 8;
                int dz = serverLevel.getRandom().nextInt(16) - 8;
                BlockPos fluxPos = worldPosition.offset(dx, dy, dz);
                if (serverLevel.isEmptyBlock(fluxPos)
                        && Compat.port().flux().spill(serverLevel, fluxPos, dy > 0)
                        && Compat.port().fx().isAvailable()) {
                    Compat.port().fx().arcLightning(serverLevel,
                        Vec3.atCenterOf(worldPosition), Vec3.atCenterOf(fluxPos), 0xFFFFFF);
                }
            }
            if (transducers > 1 && count % 50 == 0 && level instanceof ServerLevel serverLevel) {
                List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class,
                    new AABB(worldPosition).inflate(10.0, 10.0, 10.0));
                if (!targets.isEmpty()) {
                    DamageSource magic = serverLevel.damageSources().magic();
                    for (LivingEntity target : targets) {
                        if (Compat.port().fx().isAvailable()) {
                            Compat.port().fx().arcLightning(serverLevel,
                                Vec3.atCenterOf(worldPosition),
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ()),
                                0xFFFFFF);
                        }
                        target.hurtServer(serverLevel, magic, 4.0F);
                    }
                }
            }
            if (level.getBlockEntity(new BlockPos(ecks, why, zee)) instanceof Node
                && !(level.getBlockEntity(new BlockPos(ecks, why, zee)) instanceof EnergizedNodeBlockEntity)) {
                for (int i = 0; i < transducers; i++) {
                    unboostNodeAt(ecks, why, zee);
                }
                activated = false;
                sync();
            }
        }
        if (activated != lastActivated) {
            lastActivated = activated;
        }
    }

    private boolean isActivatedTransducer(int x, int y, int z) {
        return level != null
            && level.getBlockState(new BlockPos(x, y, z)).is(ThBlocks.TRANSDUCTION_AMPLIFIER.get())
            && level.getBlockEntity(new BlockPos(x, y, z)) instanceof TransductionAmplifierBlockEntity amp
            && amp.activated;
    }

    void boostNode(EnergizedNodeBlockEntity node) {
        AspectList baseVis = node.getAuraBase();
        for (Aspect asp : baseVis.getAspects()) {
            baseVis.add(asp, 10);
        }
        node.setAspects(baseVis);
        node.setupNode();
    }

    public void unBoostNode() {
        Direction dir = getBlockState().hasProperty(TransductionAmplifierBlock.FACING)
            ? getBlockState().getValue(TransductionAmplifierBlock.FACING)
            : Direction.UP;
        BlockPos nodePos = worldPosition.relative(dir.getOpposite());
        if (level != null && level.getBlockEntity(nodePos) instanceof EnergizedNodeBlockEntity node) {
            unboostNode(node);
        }
    }

    void unboostNode(EnergizedNodeBlockEntity node) {
        AspectList baseVis = node.getAuraBase();
        for (Aspect asp : baseVis.getAspects()) {
            baseVis.remove(asp, 10);
        }
        node.setAspects(baseVis);
        node.setupNode();
    }

    void unboostNodeAt(int x, int y, int z) {
        if (level == null) {
            return;
        }
        if (level.getBlockEntity(new BlockPos(x, y, z)) instanceof EnergizedNodeBlockEntity node) {
            unboostNode(node);
        } else if (level.getBlockEntity(new BlockPos(x, y, z)) instanceof Node node) {
            AspectList baseVis = node.getAspectsBase();
            for (Aspect asp : baseVis.getAspects()) {
                baseVis.remove(asp, 10);
            }
            node.setAspects(baseVis);
        }
    }

    @Override
    public void setRemoved() {
        if (activated) {
            unBoostNode();
        }
        super.setRemoved();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        activated = input.getBooleanOr("active", false);
        shouldActivate = input.getBooleanOr("shouldactivate", false);
        count = input.getIntOr("count", -1);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("active", activated);
        output.putBoolean("shouldactivate", shouldActivate);
        output.putInt("count", count);
    }
}
