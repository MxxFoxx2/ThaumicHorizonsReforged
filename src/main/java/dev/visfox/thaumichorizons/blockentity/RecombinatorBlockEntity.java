package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.lib.NodeRecombination;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.node.Node;

public final class RecombinatorBlockEntity extends ThBlockEntity {
    private int count = -1;
    private boolean activated;
    private boolean shouldActivate;
    private boolean fireOnce;

    public RecombinatorBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.RECOMBINATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RecombinatorBlockEntity entity) {
        entity.tick();
    }

    private void tick() {
        if (level == null) {
            return;
        }
        if (!fireOnce) {
            fireOnce = true;
            if (!level.isClientSide()) {
                shouldActivate = level.hasNeighborSignal(worldPosition);
            }
        }
        if (activated) {
            count++;
        } else if (count > 0) {
            if (count > 50) {
                count = 50;
            }
            count--;
        }
        if (shouldActivate && !activated) {
            activated = true;
            sync();
        } else if (!shouldActivate && activated) {
            activated = false;
            sync();
        }
        if (!(level instanceof ServerLevel serverLevel) || !activated || count <= 50
            || !(level.getBlockEntity(worldPosition.below()) instanceof Node)) {
            return;
        }
        int offsetX = serverLevel.getRandom().nextInt(5) - serverLevel.getRandom().nextInt(5);
        int offsetY = serverLevel.getRandom().nextInt(5) - serverLevel.getRandom().nextInt(5) - 1;
        int offsetZ = serverLevel.getRandom().nextInt(5) - serverLevel.getRandom().nextInt(5);
        if (offsetX == 0 && offsetY == -1 && offsetZ == 0) {
            return;
        }
        NodeRecombination.tryRecombine(serverLevel, worldPosition, worldPosition.offset(offsetX, offsetY, offsetZ));
    }

    public void updatePower(boolean powered) {
        if (activated && !powered) {
            shouldActivate = false;
        } else if (!activated && powered) {
            shouldActivate = true;
        }
    }

    public boolean isActivated() {
        return activated;
    }

    public int getCount() {
        return count;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        activated = input.getBooleanOr("active", false);
        shouldActivate = input.getBooleanOr("shouldactivate", false);
        count = input.getIntOr("count", -1);
        fireOnce = false;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("active", activated);
        output.putBoolean("shouldactivate", shouldActivate);
        output.putInt("count", count);
    }
}
