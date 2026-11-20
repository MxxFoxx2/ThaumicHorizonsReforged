package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public final class VortexBlockItem extends BlockItem {
    private final boolean cheat;

    public VortexBlockItem(Block block, Properties properties, boolean cheat) {
        super(block, properties);
        this.cheat = cheat;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (cheat && result.consumesAction()
            && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof VortexBlockEntity vortex) {
            vortex.cheat = true;
            vortex.sync();
        }
        return result;
    }
}
