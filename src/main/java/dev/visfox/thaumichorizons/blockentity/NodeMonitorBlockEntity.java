package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.block.NodeMonitorBlock;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.aspect.Aspect;
import thaumcraft.node.Node;

public class NodeMonitorBlockEntity extends ThBlockEntity {
    public boolean activated;
    boolean lastActivated;
    public int rotation;
    public boolean switchy;

    public NodeMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.NODE_MONITOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, NodeMonitorBlockEntity entity) {
        entity.tick();
    }

    private void tick() {
        if (!activated) {
            switchy = false;
            rotation++;
            if (rotation > 360) {
                rotation -= 360;
            }
        } else if (level != null && level.isClientSide() && level.getGameTime() % 15 == 0) {
            switchy = !switchy;
        }
        Direction dir = getBlockState().hasProperty(NodeMonitorBlock.FACING)
            ? getBlockState().getValue(NodeMonitorBlock.FACING)
            : Direction.UP;
        BlockPos nodePos = worldPosition.relative(dir.getOpposite());
        if (level != null && level.getBlockEntity(nodePos) instanceof Node node) {
            activated = aspectCritical(node);
        } else if (level != null && !level.isClientSide()) {
            level.destroyBlock(worldPosition, true);
            return;
        }
        if (level != null && activated != lastActivated) {
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
        lastActivated = activated;
    }

    private boolean aspectCritical(Node node) {
        for (Aspect asp : node.getAspects().getAspects()) {
            if (node.getAspects().getAmount(asp) <= 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        activated = input.getBooleanOr("active", false);
        lastActivated = input.getBooleanOr("lastactive", false);
        rotation = input.getIntOr("rotation", 0);
        switchy = input.getBooleanOr("switchy", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("active", activated);
        output.putBoolean("lastactive", lastActivated);
        output.putInt("rotation", rotation);
        output.putBoolean("switchy", switchy);
    }
}
