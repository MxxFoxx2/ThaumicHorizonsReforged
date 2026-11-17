package dev.visfox.thaumichorizons.block.pocket;

import dev.visfox.thaumichorizons.world.pocket.SlotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class SlotBlock extends BaseEntityBlock {
    public SlotBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SlotBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != dev.visfox.thaumichorizons.registry.PocketRegistry.SLOT_BE.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, entity) -> SlotBlockEntity.serverTick(tickLevel, tickPos, tickState,
            (SlotBlockEntity) entity);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SlotBlockEntity slot) {
            return slot.useWithoutItem(player);
        }
        return InteractionResult.SUCCESS;
    }
}