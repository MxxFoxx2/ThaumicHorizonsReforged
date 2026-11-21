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

/** Draws creature infusion recipes (aspects, catalysts, instability) on their dedicated research pages. */
@Mixin(targets = "thaumcraft.client.research.ResearchPageScreen")
abstract class CreatureInfusionResearchPageMixin {
    private static final String PAGE_PREFIX = "textures/misc/creature_page/";
    private static final int CONTENT_WIDTH = 138;
    private static final int GRID_TOP = 28;
    private static final int MAX_SHOWN_COMPONENTS = 7;

    @Inject(method = "drawPage", at = @At("TAIL"))
    private void thaumichorizons$drawCreaturePage(GuiGraphicsExtractor graphics, @Coerce Object display,
        int side, int x, int y, CallbackInfo callback) {
        if (!(display instanceof DisplayPageAccessor accessor)
            || !(accessor.thaumichorizons$getSource() instanceof ResearchPage.Image image)
            || !ThaumicHorizons.MODID.equals(image.image().getNamespace())
            || !image.image().getPath().startsWith(PAGE_PREFIX)) {
            return;
        }

        ThInfusionRecipes.CreatureInfusionRecipe recipe = recipe(image.image().getPath());
        if (recipe == null) {
            return;
        }

        int pageX = x - 15 + side * 152;
        Minecraft minecraft = Minecraft.getInstance();

        graphics.centeredText(minecraft.font, title(recipe), pageX + CONTENT_WIDTH / 2, y + 9, 0xFF505050);

        thaumcraft.aspect.AspectList aspects = recipe.getAspects();
        int rows = AspectGrid.rows(aspects, 4);
        AspectGrid.draw(graphics, aspects, 4, CONTENT_WIDTH, pageX + 7, y + GRID_TOP);

        ItemStack[] components = recipe.getComponents();
        int componentsTop = y + GRID_TOP + rows * AspectGrid.SIZE + 12;
        int shown = Math.min(components.length, MAX_SHOWN_COMPONENTS);
        int startX = pageX + 7 + (CONTENT_WIDTH - shown * AspectGrid.SIZE) / 2;
        for (int index = 0; index < shown; index++) {
            if (!components[index].isEmpty()) {
                graphics.item(components[index], startX + index * AspectGrid.SIZE, componentsTop);
            }
        }

        graphics.centeredText(minecraft.font,
            Component.translatable("thaumichorizons.research.critter.instability", recipe.getInstability()),
            pageX + CONTENT_WIDTH / 2, componentsTop + 22, 0xFF777777);
    }

    private static ThInfusionRecipes.@Nullable CreatureInfusionRecipe recipe(String path) {
        String rest = path.substring(PAGE_PREFIX.length());                 // "<research>/<index>.png"
        int slash = rest.indexOf('/');
        if (slash <= 0 || !rest.endsWith(".png")) {
            return null;
        }
        String research = rest.substring(0, slash);
        int index;
        try {
            index = Integer.parseInt(rest.substring(slash + 1, rest.length() - 4));
        } catch (NumberFormatException exception) {
            return null;
        }
        int seen = 0;
        for (ThInfusionRecipes.CreatureInfusionRecipe candidate : ThInfusionRecipes.getCritterRecipes()) {
            if (candidate.getResearch().equalsIgnoreCase(research)) {
                if (seen == index) {
                    return candidate;
                }
                seen++;
            }
        }
        return null;
    }

    private static Component title(ThInfusionRecipes.CreatureInfusionRecipe recipe) {
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
}
