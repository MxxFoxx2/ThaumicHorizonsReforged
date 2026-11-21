package dev.visfox.thaumichorizons.client.compat.tc.mixin;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.infusion.ThInfusionRecipes;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.client.recipeview.AspectGrid;
import thaumcraft.research.ResearchPage;

/** Draws creature and self infusion recipes (aspects, catalysts, instability) on their dedicated research pages. */
@Mixin(targets = "thaumcraft.client.research.ResearchPageScreen")
abstract class CreatureInfusionResearchPageMixin {
    private static final String CREATURE_PAGE_PREFIX = "textures/misc/creature_page/";
    private static final String SELF_PAGE_PREFIX = "textures/misc/self_page/";
    private static final int CONTENT_WIDTH = 138;
    private static final int GRID_TOP = 28;
    private static final int MAX_SHOWN_COMPONENTS = 7;

    @Inject(method = "drawPage", at = @At("TAIL"))
    private void thaumichorizons$drawInfusionPage(GuiGraphicsExtractor graphics, @Coerce Object display,
        int side, int x, int y, CallbackInfo callback) {
        if (!(display instanceof DisplayPageAccessor accessor)
            || !(accessor.thaumichorizons$getSource() instanceof ResearchPage.Image image)
            || !ThaumicHorizons.MODID.equals(image.image().getNamespace())) {
            return;
        }

        String path = image.image().getPath();
        String marker = markerSuffix(path);
        if (marker == null) {
            return;
        }
        String research = marker.substring(0, marker.indexOf('/'));
        int index;
        try {
            index = Integer.parseInt(marker.substring(marker.indexOf('/') + 1, marker.length() - 4));
        } catch (NumberFormatException exception) {
            return;
        }

        if (path.startsWith(CREATURE_PAGE_PREFIX)) {
            ThInfusionRecipes.CreatureInfusionRecipe recipe = recipe(ThInfusionRecipes.getCritterRecipes(),
                research, index, CreatureInfusionResearchPageMixin::creatureRecipeKey);
            if (recipe != null) {
                draw(graphics, side, x, y, recipe.getAspects(), recipe.getComponents(), recipe.getInstability(),
                    creatureTitle(recipe));
            }
        } else {
            ThInfusionRecipes.SelfInfusionRecipe recipe = recipe(ThInfusionRecipes.getSelfRecipes(),
                research, index, ThInfusionRecipes.SelfInfusionRecipe::getResearch);
            if (recipe != null) {
                draw(graphics, side, x, y, recipe.getAspects(), recipe.getComponents(), recipe.getInstability(),
                    Component.translatable("tc.research_name." + research));
            }
        }
    }

    /** Marker paths are lowercase because {@code Identifier} rejects camelCase; research keys compare case-insensitively. */
    private static @Nullable String markerSuffix(String path) {
        if (path.startsWith(CREATURE_PAGE_PREFIX)) {
            return path.substring(CREATURE_PAGE_PREFIX.length());
        }
        if (path.startsWith(SELF_PAGE_PREFIX)) {
            return path.substring(SELF_PAGE_PREFIX.length());
        }
        return null;
    }

    private interface RecipeKey<T> {
        String key(T recipe);
    }

    private static <T> @Nullable T recipe(Iterable<T> recipes, String research, int index, RecipeKey<T> key) {
        int seen = 0;
        for (T candidate : recipes) {
            if (key.key(candidate).equalsIgnoreCase(research)) {
                if (seen == index) {
                    return candidate;
                }
                seen++;
            }
        }
        return null;
    }

    private static void draw(GuiGraphicsExtractor graphics, int side, int x, int y,
        thaumcraft.aspect.AspectList aspects, ItemStack[] components, int instability, Component title) {
        int pageX = x - 15 + side * 152;
        Minecraft minecraft = Minecraft.getInstance();

        graphics.centeredText(minecraft.font, title, pageX + CONTENT_WIDTH / 2, y + 9, 0xFF505050);

        int rows = AspectGrid.rows(aspects, 4);
        AspectGrid.draw(graphics, aspects, 4, CONTENT_WIDTH, pageX + 7, y + GRID_TOP);

        int componentsTop = y + GRID_TOP + rows * AspectGrid.SIZE + 12;
        int shown = Math.min(components.length, MAX_SHOWN_COMPONENTS);
        int startX = pageX + 7 + (CONTENT_WIDTH - shown * AspectGrid.SIZE) / 2;
        for (int index = 0; index < shown; index++) {
            if (!components[index].isEmpty()) {
                graphics.item(components[index], startX + index * AspectGrid.SIZE, componentsTop);
            }
        }

        graphics.centeredText(minecraft.font,
            Component.translatable("thaumichorizons.research.critter.instability", instability),
            pageX + CONTENT_WIDTH / 2, componentsTop + 22, 0xFF777777);
    }

    private static Component creatureTitle(ThInfusionRecipes.CreatureInfusionRecipe recipe) {
        Object output = recipe.getRecipeOutput();
        if (output instanceof Integer id && id != 0) {
            EntityType<?> type = ThInfusionRecipes.outputEntityType(id);
            if (type != null) {
                return Component.translatable(type.getDescriptionId());
            }
        }
        if (output instanceof Map<?, ?> mods) {
            for (Object key : mods.keySet()) {
                if (key instanceof String tag && !tag.startsWith("generic.")) {
                    return Component.translatable("critterInfusions." + tag);
                }
            }
        }
        return Component.translatable(recipe.getResearch());
    }

    private static String creatureRecipeKey(ThInfusionRecipes.CreatureInfusionRecipe recipe) {
        return recipe.getResearch();
    }
}
