package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.block.CloudBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class CloudBlockItem extends BlockItem {
    private final int type;

    public CloudBlockItem(Block block, int type, Properties properties) {
        super(block, properties);
        this.type = type;
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        return state == null ? null : state.setValue(CloudBlock.TYPE, type);
    }
}
