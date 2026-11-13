package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.TransductionAmplifierBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;
import thaumcraft.node.EnergizedNodeBlockEntity;

public class TransductionAmplifierBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public TransductionAmplifierBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(pos.relative(dir)) instanceof EnergizedNodeBlockEntity) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (context.getLevel().getBlockEntity(context.getClickedPos().relative(face.getOpposite()))
            instanceof EnergizedNodeBlockEntity) {
            return defaultBlockState().setValue(FACING, face);
        }
        return null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
        @Nullable Orientation orientation, boolean movedByPiston) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof TransductionAmplifierBlockEntity amplifier) {
            boolean powered = level.hasNeighborSignal(pos);
            if (amplifier.activated && !powered) {
                amplifier.shouldActivate = false;
            } else if (!amplifier.activated && powered) {
                amplifier.shouldActivate = true;
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TransductionAmplifierBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        if (type != ThBlockEntities.TRANSDUCTION_AMPLIFIER.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, entity) -> TransductionAmplifierBlockEntity.tick(tickLevel, pos, tickState,
            (TransductionAmplifierBlockEntity) entity);
    }
}
