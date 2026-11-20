package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thaumcraft.node.Node;

public final class NodeCheatItem extends Item {
    public NodeCheatItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof Node node)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        level.setBlock(pos, ThBlocks.VORTEX.get().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof VortexBlockEntity vortex) {
            vortex.aspects = node.getAspectsBase().copy();
            vortex.cheat = true;
            vortex.sync();
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }
}
