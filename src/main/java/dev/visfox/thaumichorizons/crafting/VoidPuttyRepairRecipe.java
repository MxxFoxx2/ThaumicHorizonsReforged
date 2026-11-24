package dev.visfox.thaumichorizons.crafting;

import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class VoidPuttyRepairRecipe extends CustomRecipe {
    public VoidPuttyRepairRecipe() {
        super(net.minecraft.world.item.crafting.CraftingBookCategory.MISC);
    }

    public static final VoidPuttyRepairRecipe INSTANCE = new VoidPuttyRepairRecipe();
    private static final MapCodec<VoidPuttyRepairRecipe> CODEC = MapCodec.unit(INSTANCE);
    private static final StreamCodec<RegistryFriendlyByteBuf, VoidPuttyRepairRecipe> STREAM_CODEC =
        StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<VoidPuttyRepairRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<VoidPuttyRepairRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, VoidPuttyRepairRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack target = null;
        int putty = 0;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.isDamageableItem() && stack.isDamaged()) {
                if (target != null) {
                    return false;
                }
                target = stack;
            } else if (stack.is(ThItems.VOID_PUTTY.get())) {
                putty++;
            } else {
                return false;
            }
        }
        return target != null && putty > 0;
    }

    @Override
    public ItemStack assemble(CraftingInput input, net.minecraft.core.HolderLookup.Provider registries) {
        ItemStack target = null;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (!stack.isEmpty() && stack.isDamageableItem() && stack.isDamaged()) {
                if (target != null || !stack.has(DataComponents.REPAIR_COST)) {
                    return ItemStack.EMPTY;
                }
                target = stack;
            }
        }
        if (target == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = target.copyWithCount(1);
        result.setDamageValue(0);
        return result;
    }

    @Override
    public RecipeSerializer<VoidPuttyRepairRecipe> getSerializer() {
        return SERIALIZER;
    }
}
