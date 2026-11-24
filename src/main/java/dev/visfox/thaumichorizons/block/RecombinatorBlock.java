package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.RecombinatorBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

public final class RecombinatorBlock extends Block implements EntityBlock {
    public RecombinatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
        BlockPos sourcePos, boolean movedByPiston) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof RecombinatorBlockEntity recombinator) {
            recombinator.updatePower(level.hasNeighborSignal(pos));
        }
        super.neighborChanged(state, level, pos, neighborBlock, sourcePos, movedByPiston);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RecombinatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        if (type != ThBlockEntities.RECOMBINATOR.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, entity) -> RecombinatorBlockEntity.tick(tickLevel, pos, tickState,
            (RecombinatorBlockEntity) entity);
    }
}
