package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.VisDynamoBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class VisDynamoBlock extends Block implements EntityBlock {
    public VisDynamoBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VisDynamoBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        if (type != ThBlockEntities.VIS_DYNAMO.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, entity) -> VisDynamoBlockEntity.tick(tickLevel, pos, tickState,
            (VisDynamoBlockEntity) entity);
    }
}
