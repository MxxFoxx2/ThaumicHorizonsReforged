package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.NodeMonitorBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import thaumcraft.node.Node;

public class NodeMonitorBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0);

    public NodeMonitorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(pos.relative(dir)) instanceof Node) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (context.getLevel().getBlockEntity(context.getClickedPos().relative(face.getOpposite())) instanceof Node) {
            return defaultBlockState().setValue(FACING, face);
        }
        return null;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof NodeMonitorBlockEntity monitor && monitor.activated ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof NodeMonitorBlockEntity monitor && monitor.activated ? 15 : 0;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NodeMonitorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        if (type != ThBlockEntities.NODE_MONITOR.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, entity) -> NodeMonitorBlockEntity.tick(tickLevel, pos, tickState,
            (NodeMonitorBlockEntity) entity);
    }
}
