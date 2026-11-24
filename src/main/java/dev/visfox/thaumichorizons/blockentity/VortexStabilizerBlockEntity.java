package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.block.VortexStabilizerBlock;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import thaumcraft.node.Node;
import thaumcraft.node.NodeType;

public class VortexStabilizerBlockEntity extends ThBlockEntity {
    public boolean hasTarget;
    public int prevType;
    public BlockPos targetPos;
    public BlockEntity target;
    public boolean redstoned;
    private boolean lastSyncedInit;

    public VortexStabilizerBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.VORTEX_STABILIZER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VortexStabilizerBlockEntity entity) {
        entity.tick();
    }

    private void tick() {
        if (level == null) {
            return;
        }
        Direction dir = getBlockState().hasProperty(VortexStabilizerBlock.FACING)
            ? getBlockState().getValue(VortexStabilizerBlock.FACING)
            : Direction.UP;
        if (!lastSyncedInit) {
            lastSyncedInit = true;
            if (target == null && targetPos != null) {
                target = level.getBlockEntity(targetPos);
            }
        }
        if (level.getGameTime() % 5L == 0L) {
            BlockHitResult mop = null;
            if (redstoned) {
                Vec3 from = new Vec3(
                    worldPosition.getX() + dir.getStepX() + 0.75,
                    worldPosition.getY() + dir.getStepY() + 0.75,
                    worldPosition.getZ() + dir.getStepZ() + 0.75);
                Vec3 to = new Vec3(
                    worldPosition.getX() + dir.getStepX() * 10 + 0.5,
                    worldPosition.getY() + dir.getStepY() * 10 + 0.5,
                    worldPosition.getZ() + dir.getStepZ() * 10 + 0.5);
                // Original rayTraceBlocks(from,to) hits blocks without a collision box (vortex
                // collision is null upstream); COLLIDER would ray straight through the vortex.
                HitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE, CollisionContext.empty()));
                if (hit instanceof BlockHitResult blockHit) {
                    mop = blockHit;
                }
            }
            if (mop != null) {
                if (!mop.getBlockPos().equals(targetPos)) {
                    if (hasTarget) {
                        reHungrifyTarget();
                        hasTarget = false;
                    }
                    BlockEntity newTE = level.getBlockEntity(mop.getBlockPos());
                    if (newTE instanceof Node) {
                        hasTarget = true;
                        target = newTE;
                        prevType = ((Node) newTE).getNodeType().ordinal();
                        deHungrifyTarget();
                    } else if (newTE instanceof VortexBlockEntity) {
                        hasTarget = true;
                        target = newTE;
                        deHungrifyTarget();
                    }
                    targetPos = mop.getBlockPos();
                    sync();
                }
            } else {
                if (hasTarget) {
                    reHungrifyTarget();
                    hasTarget = false;
                    sync();
                }
                targetPos = worldPosition.relative(dir, 10);
                target = null;
            }
        }
    }

    public void reHungrifyTarget() {
        if (level != null && target instanceof Node) {
            if (!isNodeTargetedByOtherStabilizer()) {
                ((Node) target).setNodeType(NodeType.values()[prevType]);
            }
        } else if (target instanceof VortexBlockEntity vortex) {
            vortex.beams--;
        }
        if (target != null) {
            target.setChanged();
        }
    }

    private boolean isNodeTargetedByOtherStabilizer() {
        if (target == null) {
            return false;
        }
        int tx = target.getBlockPos().getX();
        int ty = target.getBlockPos().getY();
        int tz = target.getBlockPos().getZ();
        for (Direction d : Direction.values()) {
            for (int dist = 1; dist <= 10; dist++) {
                BlockEntity te = level != null
                    ? level.getBlockEntity(new BlockPos(tx - d.getStepX() * dist, ty - d.getStepY() * dist, tz - d.getStepZ() * dist))
                    : null;
                if (te instanceof VortexStabilizerBlockEntity other && te != this
                    && other.hasTarget && other.target == target) {
                    return true;
                }
            }
        }
        return false;
    }

    void deHungrifyTarget() {
        if (target instanceof Node) {
            ((Node) target).setNodeType(NodeType.NORMAL);
        } else if (target instanceof VortexBlockEntity vortex) {
            vortex.beams++;
        }
        if (target != null) {
            target.setChanged();
        }
    }

    @Override
    public void setRemoved() {
        if (hasTarget) {
            reHungrifyTarget();
        }
        super.setRemoved();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int xT = input.getIntOr("xT", Integer.MAX_VALUE);
        int yT = input.getIntOr("yT", Integer.MAX_VALUE);
        int zT = input.getIntOr("zT", Integer.MAX_VALUE);
        targetPos = new BlockPos(xT, yT, zT);
        hasTarget = input.getBooleanOr("hasTarget", false);
        redstoned = input.getBooleanOr("active", false);
        prevType = input.getIntOr("prevType", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("xT", targetPos != null ? targetPos.getX() : Integer.MAX_VALUE);
        output.putInt("yT", targetPos != null ? targetPos.getY() : Integer.MAX_VALUE);
        output.putInt("zT", targetPos != null ? targetPos.getZ() : Integer.MAX_VALUE);
        output.putBoolean("hasTarget", hasTarget);
        output.putBoolean("active", redstoned);
        output.putInt("prevType", prevType);
    }
}
