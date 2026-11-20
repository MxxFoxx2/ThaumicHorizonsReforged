package dev.visfox.thaumichorizons.item.golem;

import dev.visfox.thaumichorizons.item.focus.FocusAnimationItem;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;


public class GolemPowderItem extends Item {
    public GolemPowderItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        BlockState state = level.getBlockState(pos);
        if (state.hasBlockEntity() || state.isAir() || state.getDestroySpeed(level, pos) < 0.0F) {
            return InteractionResult.FAIL;
        }
        if (!state.isSolidRender() && !FocusAnimationItem.isWhitelisted(state)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        EntityGolemTH golem = new EntityGolemTH(EntityGolemTH.thType(), level);
        golem.loadGolem(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state, 0, 1200, false, false, false);
        if (CommonHooks.fireBlockBreak(serverLevel, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer, pos,
            state).isCanceled()) {
            golem.discard();
            return InteractionResult.FAIL;
        }
        level.removeBlock(pos, false);
        THSounds.play(level, net.minecraft.world.phys.Vec3.atCenterOf(pos), "wand", SoundSource.PLAYERS, 1.0F, 1.0F);
        golem.setHome(pos, context.getClickedFace());
        golem.setOwnerUUID(player.getUUID());
        level.addFreshEntity(golem);
        level.broadcastEntityEvent(golem, (byte) 7);
        if (!(player.getAbilities().instabuild)) {
            context.getItemInHand().consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }
}
