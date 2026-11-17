package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.Nullable;

/**
 * Uses the color persisted on the bound Keystone itself. This avoids trying
 * to read server-only PocketPlanes SavedData from a remote client.
 */
public record KeystoneTintSource() implements ItemTintSource {
    public static final MapCodec<KeystoneTintSource> MAP_CODEC = MapCodec.unit(new KeystoneTintSource());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return 0xFFFFFFFF;
        }
        int color = customData.copyTag().getIntOr("color", 0xFFFFFF);
        return color | 0xFF000000;
    }

    @Override
    public MapCodec<KeystoneTintSource> type() {
        return MAP_CODEC;
    }
}
