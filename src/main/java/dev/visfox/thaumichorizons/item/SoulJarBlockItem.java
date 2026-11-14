package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.block.SoulJarBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class SoulJarBlockItem extends BlockItem {
    public SoulJarBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        SoulJarKeys.SoulJarData data = SoulJarKeys.DATA.get(stack);
        if (data == null || data.critterName().isEmpty() && !data.soul()) {
            return Component.translatable("item.thaumichorizons.jarredSoul.0.name");
        }
        String name = Component.translatable("item.thaumichorizons.jarredSoul.jarred").getString();
        name += " " + data.critterName();
        if (data.soul()) {
            name += " " + Component.translatable("item.thaumichorizons.jarredSoul.soul").getString();
        }
        return Component.literal(name);
    }
}
