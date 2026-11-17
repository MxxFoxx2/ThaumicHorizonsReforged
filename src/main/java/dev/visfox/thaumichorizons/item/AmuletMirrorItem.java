package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.compat.Compat;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class AmuletMirrorItem extends Item {
    public AmuletMirrorItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return Compat.port().mirrors().isAvailable() && Compat.port().mirrors().isLinked(stack);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null || !Compat.port().mirrors().isAvailable() || !Compat.port().mirrors().isMirror(level, pos)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            Compat.port().mirrors().link(stack, player, level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        if (Compat.port().mirrors().isAvailable() && Compat.port().mirrors().isLinked(stack)) {
            Compat.port().mirrors().appendLink(stack, builder);
        }
        super.appendHoverText(stack, context, display, builder, flag);
    }

    public static boolean transport(ItemStack mirror, ItemStack items, Player player, Level level) {
        return Compat.port().mirrors().isAvailable() && Compat.port().mirrors().transport(mirror, items, player, level);
    }
}
