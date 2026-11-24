package dev.visfox.thaumichorizons.registry;

import java.util.function.UnaryOperator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

final class THRegistryProps {
    private THRegistryProps() {
    }

    static BlockBehaviour.Properties block(UnaryOperator<BlockBehaviour.Properties> configure) {
        return configure.apply(BlockBehaviour.Properties.of());
    }

    static Item.Properties item(UnaryOperator<Item.Properties> configure) {
        return configure.apply(new Item.Properties());
    }
}
