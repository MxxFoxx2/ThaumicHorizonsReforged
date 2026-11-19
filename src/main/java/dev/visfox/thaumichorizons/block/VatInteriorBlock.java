package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.VatSlaveBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class VatInteriorBlock extends Block implements EntityBlock {

    public VatInteriorBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VatSlaveBlockEntity(ThBlockEntities.VAT_SLAVE.get(), pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
        BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof VatSlaveBlockEntity slave) {
            return slave.activate(player) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return InteractionResult.FAIL;
    }


    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
        net.minecraft.world.entity.InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        entity.setAirSupply(300);
    }
}
