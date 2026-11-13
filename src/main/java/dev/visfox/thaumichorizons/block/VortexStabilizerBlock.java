package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.VortexStabilizerBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import thaumcraft.wand.WandInteractable;

public class VortexStabilizerBlock extends Block implements EntityBlock, WandInteractable {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public VortexStabilizerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public InteractionResult onWandUseOn(Level level, BlockPos pos, Player player, ItemStack wand, Direction side) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != this) {
            return InteractionResult.PASS;
        }
        level.setBlock(pos, state.setValue(FACING, side), Block.UPDATE_ALL);
        if (level.getBlockEntity(pos) instanceof VortexStabilizerBlockEntity stabilizer) {
            stabilizer.sync();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
        @Nullable Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof VortexStabilizerBlockEntity stabilizer) {
            stabilizer.redstoned = level.hasNeighborSignal(pos);
            stabilizer.sync();
        }
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VortexStabilizerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        if (type != ThBlockEntities.VORTEX_STABILIZER.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, entity) -> VortexStabilizerBlockEntity.tick(tickLevel, pos, tickState,
            (VortexStabilizerBlockEntity) entity);
    }
}
