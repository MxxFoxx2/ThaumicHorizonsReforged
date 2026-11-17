package dev.visfox.thaumichorizons.compat.tc;

import dev.visfox.thaumichorizons.item.AmuletMirrorItem;
import net.minecraft.world.item.ItemStack;
import thaumcraft.platform.Bauble;
import thaumcraft.platform.BaubleType;

public final class BaubleAmuletMirrorItem extends AmuletMirrorItem implements Bauble {
    public BaubleAmuletMirrorItem(Properties properties) {
        super(properties);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.AMULET;
    }
}
