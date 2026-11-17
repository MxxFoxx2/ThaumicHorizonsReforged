package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.SpikeBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * The three legacy spike materials share one directional implementation.
 * Facing is stored as a block-state property (the old TileSpike dir byte:
 * DOWN=0, UP=1, NORTH=2, SOUTH=3, WEST=4, EAST=5).
 */
public final class SpikeBlock extends Block implements net.minecraft.world.level.block.EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    private static final VoxelShape DOWN_SHAPE = Block.box(0, 1, 0, 16, 16, 16);
    private static final VoxelShape UP_SHAPE = Block.box(0, 0, 0, 16, 15, 16);
    private static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 1, 16, 16, 16);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 0, 16, 16, 15);
    private static final VoxelShape WEST_SHAPE = Block.box(1, 0, 0, 16, 16, 16);
    private static final VoxelShape EAST_SHAPE = Block.box(0, 0, 0, 15, 16, 16);

    private final int spikeType;

    public SpikeBlock(Properties properties, int spikeType) {
        super(properties);
        if (spikeType < 0 || spikeType > 2) {
            throw new IllegalArgumentException("Unknown spike type: " + spikeType);
        }
        this.spikeType = spikeType;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    public int getSpikeType() {
        return spikeType;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockState state = defaultBlockState().setValue(FACING, facing);
        return canSurvive(state, context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        BlockState support = level.getBlockState(supportPos);
        return support.isFaceSturdy(level, supportPos, facing);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
        BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
        RandomSource random) {
        if (directionToNeighbour == state.getValue(FACING).getOpposite()) {
            ticks.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected void tick(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos,
        RandomSource random) {
        if (!canSurvive(state, level, pos) && level.getBlockState(pos).is(this)) {
            Block.dropResources(state, level, pos);
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
        CollisionContext context) {
        return shape(state.getValue(FACING));
    }

    private static VoxelShape shape(Direction facing) {
        return switch (facing) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
        InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        entity.hurt(level.damageSources().cactus(), 1.0F);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpikeBlockEntity(pos, state);
    }
}
