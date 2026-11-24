package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Uses the color persisted on the bound Keystone itself. This avoids trying
 * to read server-only PocketPlanes SavedData from a remote client.
 */
public record KeystoneTintSource() implements ItemColor {
    public static final KeystoneTintSource INSTANCE = new KeystoneTintSource();
    public static final MapCodec<KeystoneTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return 0xFFFFFFFF;
        }
        int color = customData.copyTag().getInt("color");
        return color | 0xFF000000;
    }
}
