package dev.visfox.thaumichorizons.item.focus;

import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import dev.visfox.thaumichorizons.lib.THSounds;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.CommonHooks;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.Wands;
import thaumcraft.wand.focus.FocusItem;
import thaumcraft.wand.focus.FocusUpgradeType;

public class FocusAnimationItem extends FocusItem {
    public static final FocusUpgradeType BERSERK = new FocusUpgradeType(30, "berserk",
        ResourceLocation.fromNamespaceAndPath("thaumichorizons", "textures/foci/berserk.png"),
        new AspectList().add(Aspect.WEAPON, 8));
    public static final FocusUpgradeType DETONATION = new FocusUpgradeType(31, "detonation",
        ResourceLocation.fromNamespaceAndPath("thaumichorizons", "textures/foci/detonation.png"),
        new AspectList().add(Aspect.WEAPON, 8));

    private static final AspectList COST = new AspectList()
        .add(Aspect.FIRE, 1000)
        .add(Aspect.AIR, 1000)
        .add(Aspect.ORDER, 1000)
        .add(Aspect.WATER, 1000)
        .add(Aspect.EARTH, 1000)
        .add(Aspect.ENTROPY, 1000);

    public FocusAnimationItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getFocusColor(ItemStack focus) {
        return 15054592;
    }

    @Override
    public AspectList getVisCost(ItemStack focus) {
        return COST;
    }

    @Override
    public @Nullable FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focus, int rank) {
        return switch (rank) {
            case 1, 2, 4 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.EXTEND};
            case 3 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.EXTEND, BERSERK};
            case 5 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.EXTEND, DETONATION};
            default -> null;
        };
    }

    @Override
    public InteractionResult onFocusRightClick(ItemStack stack, Level level, Player player, @Nullable HitResult hit) {
        if (!(hit instanceof BlockHitResult blockHit)) {
            return InteractionResult.PASS;
        }
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.hasBlockEntity() || state.isAir() || state.getDestroySpeed(level, pos) < 0.0F) {
            return InteractionResult.PASS;
        }
        if (!state.isSolidRender(level, pos) && !isWhitelisted(state)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            player.swing(InteractionHand.MAIN_HAND, true);
            return InteractionResult.CONSUME;
        }
        ItemStack focusStack = Wands.getFocusItem(stack);
        EntityGolemTH golem = new EntityGolemTH(EntityGolemTH.thType(), level);
        golem.loadGolem(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state, 0,
            600 + Wands.getFocusExtend(stack) * 200, false,
            isUpgradedWith(focusStack, BERSERK), isUpgradedWith(focusStack, DETONATION));
        AspectList cost = new AspectList();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            cost.add(aspect, golem.material().visCost);
        }
        if (!(stack.getItem() instanceof WandItem wand)
            || !wand.consumeAllVis(stack, player, cost, false, false)) {
            golem.discard();
            return InteractionResult.CONSUME;
        }
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            if (CommonHooks.fireBlockBreak(serverLevel, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer,
                pos, state).isCanceled()) {
                golem.discard();
                return InteractionResult.CONSUME;
            }
        }
        if (!wand.consumeAllVis(stack, player, cost, true, false)) {
            golem.discard();
            return InteractionResult.CONSUME;
        }
        level.removeBlock(pos, false);
        THSounds.play(level, net.minecraft.world.phys.Vec3.atCenterOf(pos), "wand", SoundSource.PLAYERS, 1.0F, 1.0F);
        golem.setHome(golem.blockPosition(), net.minecraft.core.Direction.UP);

        golem.setOwnerUUID(player.getUUID());
        level.addFreshEntity(golem);
        level.broadcastEntityEvent(golem, (byte) 7);
        return InteractionResult.CONSUME;
    }

    public static boolean isWhitelisted(BlockState state) {
        return state.is(Blocks.CAKE) || state.is(Blocks.CACTUS) || state.is(Blocks.GLASS)
            || state.is(Blocks.PACKED_ICE) || state.is(Blocks.ICE) || state.is(Blocks.COBWEB);
    }

    @Override
    public @Nullable ResourceLocation getFocusTexture(ItemStack focus) {
        return ResourceLocation.fromNamespaceAndPath("thaumichorizons", "item/focus_animation");
    }
}
