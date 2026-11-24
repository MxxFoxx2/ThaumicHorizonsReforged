package dev.visfox.thaumichorizons.client.compat.tc.mixin;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.infusion.ThInfusionRecipes;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.client.recipeview.AspectGrid;
import thaumcraft.research.ResearchPage;

/** Renders infusion recipe payloads on standard ResearchPage.Image marker pages. */
@Mixin(targets = "thaumcraft.client.research.ResearchPageScreen")
abstract class CreatureInfusionResearchPageMixin {
    private static final String CREATURE_PAGE_PREFIX = "textures/misc/creature_page/";
    private static final String SELF_PAGE_PREFIX = "textures/misc/self_page/";
    private static final int CONTENT_WIDTH = 138;
    private static final int GRID_TOP = 28;
    private static final int MAX_SHOWN_COMPONENTS = 7;

    @Inject(method = "drawPage", at = @At("HEAD"), cancellable = true)
    private void thaumichorizons$drawInfusionPage(GuiGraphics graphics, @Coerce Object display,
        int side, int x, int y, CallbackInfo callback) {
        if (!(display instanceof DisplayPageAccessor accessor)
            || !(accessor.thaumichorizons$getSource() instanceof ResearchPage.Image image)
            || !ThaumicHorizons.MODID.equals(image.image().getNamespace())) {
            return;
        }

        String path = image.image().getPath();
        if (path.startsWith(CREATURE_PAGE_PREFIX)) {
            ThInfusionRecipes.CreatureInfusionRecipe recipe = creatureRecipe(path, CREATURE_PAGE_PREFIX);
            if (recipe == null) {
                return;
            }
            draw(graphics, side, x, y, recipe.getAspects(), recipe.getComponents(), recipe.getInstability(),
                creatureTitle(recipe));
        } else if (path.startsWith(SELF_PAGE_PREFIX)) {
            ThInfusionRecipes.SelfInfusionRecipe recipe = selfRecipe(path, SELF_PAGE_PREFIX);
            if (recipe == null) {
                return;
            }
            draw(graphics, side, x, y, recipe.getAspects(), recipe.getComponents(), recipe.getInstability(),
                Component.translatable("tc.research_name." + recipe.getResearch()));
        } else {
            return;
        }

        callback.cancel();
    }

    private static @Nullable ThInfusionRecipes.CreatureInfusionRecipe creatureRecipe(String path, String prefix) {
        String marker = marker(path, prefix);
        if (marker == null) {
            return null;
        }
        String[] parts = marker.split("/", 2);
        if (parts.length != 2) {
            return null;
        }
        int index;
        try {
            index = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            return null;
        }
        int seen = 0;
        for (ThInfusionRecipes.CreatureInfusionRecipe candidate : ThInfusionRecipes.getCritterRecipes()) {
            if (candidate.getResearch().equalsIgnoreCase(parts[0])) {
                if (seen == index) {
                    return candidate;
                }
                seen++;
            }
        }
        return null;
    }

    private static @Nullable ThInfusionRecipes.SelfInfusionRecipe selfRecipe(String path, String prefix) {
        String marker = marker(path, prefix);
        if (marker == null) {
            return null;
        }
        String[] parts = marker.split("/", 2);
        if (parts.length != 2) {
            return null;
        }
        int index;
        try {
            index = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            return null;
        }
        int seen = 0;
        for (ThInfusionRecipes.SelfInfusionRecipe candidate : ThInfusionRecipes.getSelfRecipes()) {
            if (candidate.getResearch().equalsIgnoreCase(parts[0])) {
                if (seen == index) {
                    return candidate;
                }
                seen++;
            }
        }
        return null;
    }

    private static @Nullable String marker(String path, String prefix) {
        String suffix = path.substring(prefix.length());
        if (!suffix.endsWith(".png")) {
            return null;
        }
        return suffix.substring(0, suffix.length() - 4);
    }

    private static void draw(GuiGraphics graphics, int side, int x, int y,
        thaumcraft.aspect.AspectList aspects, ItemStack[] components, int instability, Component title) {
        int pageX = x - 15 + side * 152;
        Minecraft minecraft = Minecraft.getInstance();

        graphics.drawCenteredString(minecraft.font, title, pageX + CONTENT_WIDTH / 2, y + 9, 0xFF505050);

        int rows = AspectGrid.rows(aspects, 4);
        AspectGrid.draw(graphics, aspects, 4, CONTENT_WIDTH, pageX + 7, y + GRID_TOP);

        int componentsTop = y + GRID_TOP + rows * AspectGrid.SIZE + 12;
        int shown = Math.min(components.length, MAX_SHOWN_COMPONENTS);
        int startX = pageX + 7 + (CONTENT_WIDTH - shown * AspectGrid.SIZE) / 2;
        for (int index = 0; index < shown; index++) {
            if (!components[index].isEmpty()) {
                graphics.renderItem(components[index], startX + index * AspectGrid.SIZE, componentsTop);
            }
        }

        graphics.drawCenteredString(minecraft.font,
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
}
