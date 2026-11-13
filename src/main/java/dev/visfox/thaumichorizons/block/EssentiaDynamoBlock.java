package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.EssentiaDynamoBlockEntity;
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

public class EssentiaDynamoBlock extends Block implements EntityBlock {
    public EssentiaDynamoBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EssentiaDynamoBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        if (type != ThBlockEntities.ESSENTIA_DYNAMO.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, entity) -> EssentiaDynamoBlockEntity.tick(tickLevel, pos, tickState,
            (EssentiaDynamoBlockEntity) entity);
    }
}
