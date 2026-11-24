package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.IlluminationLightBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import javax.annotation.Nullable;

public class IlluminationLightBlock extends Block implements EntityBlock {
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 15);

    public IlluminationLightBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(COLOR, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IlluminationLightBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
        Level level, BlockState state, BlockEntityType<T> type
    ) {
        if (!level.isClientSide() || type != ThBlockEntities.ILLUMINATION_LIGHT.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, entity) ->
            IlluminationLightBlockEntity.clientTick(tickLevel, pos, tickState, (IlluminationLightBlockEntity) entity);
    }
}
