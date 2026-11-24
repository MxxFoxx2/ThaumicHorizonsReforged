package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.lib.InteractionCompat;

import dev.visfox.thaumichorizons.entity.AlchemitePrimedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;

public class AlchemiteBlock extends Block {
    public AlchemiteBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            if (level.hasNeighborSignal(pos) && prime(level, pos, null, ItemStack.EMPTY)) {
                level.removeBlock(pos, false);
            }
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
        BlockPos sourcePos, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos) && prime(level, pos, null, ItemStack.EMPTY)) {
            level.removeBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, neighborBlock, sourcePos, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!itemStack.is(Items.FLINT_AND_STEEL) && !itemStack.is(Items.FIRE_CHARGE)) {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
        }
        if (prime(level, pos, player, itemStack)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            if (itemStack.is(Items.FLINT_AND_STEEL)) {
                itemStack.hurtAndBreak(1, player, InteractionCompat.slot(hand));
            } else {
                itemStack.consume(1, player);
            }
            player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(itemStack.getItem()));
            return ItemInteractionResult.sidedSuccess(true);
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, net.minecraft.world.level.Explosion explosion) {
        AlchemitePrimedEntity primed = new AlchemitePrimedEntity(level,
            pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, explosion.getIndirectSourceEntity());
        primed.setFuse(level.getRandom().nextInt(primed.getFuse() / 4) + primed.getFuse() / 8);
        level.addFreshEntity(primed);
        super.wasExploded(level, pos, explosion);
    }

    public boolean onCaughtFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face,
        @Nullable LivingEntity igniter, ItemStack ignitionItem) {
        return prime(world, pos, igniter, ignitionItem);
    }

    private boolean prime(Level level, BlockPos pos, @Nullable LivingEntity igniter, ItemStack itemStack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return true;
        }
        AlchemitePrimedEntity primed = new AlchemitePrimedEntity(serverLevel,
            pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, igniter);
        serverLevel.addFreshEntity(primed);
        serverLevel.playSound(null, primed.getX(), primed.getY(), primed.getZ(),
            SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        serverLevel.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        return true;
    }
}
