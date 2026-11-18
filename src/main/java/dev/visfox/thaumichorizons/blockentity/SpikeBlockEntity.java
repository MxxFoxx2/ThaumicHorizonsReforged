package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Marker block entity used to submit the legacy OBJ spike model in world rendering.
 * The mounting direction lives in the block state and is automatically saved.
 */
public final class SpikeBlockEntity extends BlockEntity {
    public SpikeBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.SPIKE.get(), pos, state);
    }

    public int getSpikeType() {
        return ((dev.visfox.thaumichorizons.block.SpikeBlock) getBlockState().getBlock()).getSpikeType();
    }
}
