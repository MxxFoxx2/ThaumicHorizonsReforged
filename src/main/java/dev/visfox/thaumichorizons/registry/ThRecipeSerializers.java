package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.crafting.FocusIlluminationDyeRecipe;
import dev.visfox.thaumichorizons.crafting.VoidPuttyRepairRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, ThaumicHorizons.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<VoidPuttyRepairRecipe>> VOID_PUTTY_REPAIR =
        SERIALIZERS.register("void_putty_repair", () -> VoidPuttyRepairRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FocusIlluminationDyeRecipe>> FOCUS_ILLUMINATION_DYE =
        SERIALIZERS.register("focus_illumination_dye", () -> FocusIlluminationDyeRecipe.SERIALIZER);

    private ThRecipeSerializers() {
    }

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
