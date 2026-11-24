package dev.visfox.thaumichorizons.item.focus;

import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.block.IlluminationLightBlock;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.Wands;
import thaumcraft.wand.focus.FocusItem;
import thaumcraft.wand.focus.FocusUpgradeType;

public class FocusIlluminationItem extends FocusItem {
    public static final FocusUpgradeType SOLAR = new FocusUpgradeType(32, "solar",
        ResourceLocation.fromNamespaceAndPath("thaumichorizons", "textures/foci/solar.png"),
        new AspectList().add(Aspect.ORDER, 8).add(Aspect.VOID, 4));

    public static final int[] COLORS = {1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603,
        4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

    private static final AspectList COST = new AspectList().add(Aspect.FIRE, 100).add(Aspect.AIR, 100);

    public FocusIlluminationItem(Properties properties) {
        super(properties);
    }

    public static int legacyColorIndex(DyeColor color) {
        return 15 - color.getId();
    }

    @Override
    public int getFocusColor(ItemStack focus) {
        if (focus.getDamageValue() < 16) {
            return COLORS[focus.getDamageValue()];
        }
        return 420;
    }

    @Override
    public AspectList getVisCost(ItemStack focus) {
        return COST;
    }

    @Override
    public @Nullable FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focus, int rank) {
        return switch (rank) {
            case 1, 2, 4, 5 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL};
            case 3 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, SOLAR};
            default -> null;
        };
    }

    @Override
    public InteractionResult onFocusRightClick(ItemStack stack, Level level, Player player, @Nullable HitResult hit) {
        if (!(hit instanceof BlockHitResult blockHit)) {
            return InteractionResult.PASS;
        }
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (!level.getBlockState(pos).isAir()) {
            return InteractionResult.PASS;
        }
        if (!(stack.getItem() instanceof WandItem wand)
            || !wand.consumeAllVis(stack, player, COST, true, false)) {
            return InteractionResult.CONSUME;
        }
        ItemStack focus = Wands.getFocusItem(stack);
        Block light = isUpgradedWith(focus, SOLAR)
            ? ThBlocks.LIGHT_SOLAR.get()
            : ThBlocks.LIGHT.get();
        int color = Math.max(0, Math.min(COLORS.length - 1, focus.getDamageValue()));
        BlockState lightState = light.defaultBlockState().setValue(IlluminationLightBlock.COLOR, color);
        level.setBlock(pos, lightState, 3);
        player.swing(InteractionHand.MAIN_HAND, true);
        if (!level.isClientSide()) {
            THSounds.play(level, net.minecraft.world.phys.Vec3.atCenterOf(pos), "wand", SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable ResourceLocation getFocusTexture(ItemStack focus) {
        return ResourceLocation.fromNamespaceAndPath("thaumichorizons", "item/focus_illumination");
    }
}
