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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class EssentiaDynamoBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    public EssentiaDynamoBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
        net.minecraft.core.BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter level,
        net.minecraft.core.BlockPos pos, CollisionContext context) {
        return SHAPE;
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
