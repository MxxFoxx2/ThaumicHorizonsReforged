package dev.visfox.thaumichorizons.block;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

public class DropsBlock extends Block {

    private final ItemLike drop;
    private final int minCount;

    public DropsBlock(Properties properties, ItemLike drop, int minCount) {
        super(properties);
        this.drop = drop;
        this.minCount = minCount;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int count = minCount + builder.getLevel().getRandom().nextInt(3);
        return List.of(new ItemStack(drop, count));
    }
}
