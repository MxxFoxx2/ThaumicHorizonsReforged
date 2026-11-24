package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class AmuletMirrorItem extends Item {
    public AmuletMirrorItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return TcReforgedPort.mirrors.isAvailable() && TcReforgedPort.mirrors.isLinked(stack);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null || !TcReforgedPort.mirrors.isAvailable() || !TcReforgedPort.mirrors.isMirror(level, pos)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            TcReforgedPort.mirrors.link(stack, player, level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Consumer<Component> builder = tooltip::add;
        if (TcReforgedPort.mirrors.isAvailable() && TcReforgedPort.mirrors.isLinked(stack)) {
            TcReforgedPort.mirrors.appendLink(stack, builder);
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    public static boolean transport(ItemStack mirror, ItemStack items, Player player, Level level) {
        return TcReforgedPort.mirrors.isAvailable() && TcReforgedPort.mirrors.transport(mirror, items, player, level);
    }
}
