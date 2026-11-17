package dev.visfox.thaumichorizons.crafting;

import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.item.focus.FocusIlluminationItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class FocusIlluminationDyeRecipe extends CustomRecipe {
    public static final FocusIlluminationDyeRecipe INSTANCE = new FocusIlluminationDyeRecipe();
    public static final RecipeSerializer<FocusIlluminationDyeRecipe> SERIALIZER =
        new RecipeSerializer<>(MapCodec.unit(INSTANCE), StreamCodec.<RegistryFriendlyByteBuf, FocusIlluminationDyeRecipe>unit(INSTANCE));

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack focus = null;
        int dyes = 0;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof FocusIlluminationItem) {
                if (focus != null) {
                    return false;
                }
                focus = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                dyes++;
            } else {
                return false;
            }
        }
        return focus != null && dyes > 0;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack focus = null;
        int color = 0;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof FocusIlluminationItem) {
                if (focus != null) {
                    return ItemStack.EMPTY;
                }
                focus = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                DyeColor dye = stack.get(DataComponents.DYE);
                if (dye == null) {
                    return ItemStack.EMPTY;
                }
                color = FocusIlluminationItem.legacyColorIndex(dye);
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (focus == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = focus.copyWithCount(1);
        result.setDamageValue(color);
        return result;
    }

    @Override
    public RecipeSerializer<FocusIlluminationDyeRecipe> getSerializer() {
        return SERIALIZER;
    }
}
