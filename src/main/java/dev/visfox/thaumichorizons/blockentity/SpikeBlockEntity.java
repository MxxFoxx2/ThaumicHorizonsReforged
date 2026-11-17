package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Holds the legacy TileSpike identity for the custom renderer. Direction is stored in the
 * block state; the spike material is determined by the registered block variant.
 */
public final class SpikeBlockEntity extends BlockEntity {
    public SpikeBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.SPIKE.get(), pos, state);
    }

    public int getSpikeType() {
        if (getBlockState().is(ThBlocks.SPIKE_WOOD.get())) {
            return 1;
        }
        if (getBlockState().is(ThBlocks.SPIKE_TOOTH.get())) {
            return 2;
        }
        return 0;
    }
}
