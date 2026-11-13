package dev.visfox.thaumichorizons.compat;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public interface ResearchCompat {
    default boolean isAvailable() {
        return false;
    }

    default void registerCategory(String key, Identifier icon, Identifier background) {
        throw new UnsupportedOperationException();
    }

    default void registerResearch(ResearchSpec spec) {
        throw new UnsupportedOperationException();
    }

    default boolean isComplete(Player player, String researchKey) {
        throw new UnsupportedOperationException();
    }

    default void complete(Player player, String researchKey) {
        throw new UnsupportedOperationException();
    }

    public enum Flag {
        SPECIAL,
        SECONDARY,
        ROUND,
        STUB,
        VIRTUAL,
        CONCEALED,
        HIDDEN,
        LOST,
        AUTO_UNLOCK
    }

    public record ResearchSpec(
        String key,
        String category,
        CompatAspectList tags,
        int column,
        int row,
        int complexity,
        Identifier icon,
        List<Flag> flags,
        List<String> parents,
        List<String> parentsHidden,
        List<String> siblings,
        List<String> itemTriggers,
        List<String> entityTriggers,
        List<String> aspectTriggers,
        int warp,
        List<PageSpec> pages
    ) {
        public ResearchSpec {
            flags = List.copyOf(flags);
            parents = List.copyOf(parents);
            parentsHidden = List.copyOf(parentsHidden);
            siblings = List.copyOf(siblings);
            itemTriggers = List.copyOf(itemTriggers);
            entityTriggers = List.copyOf(entityTriggers);
            aspectTriggers = List.copyOf(aspectTriggers);
            pages = List.copyOf(pages);
        }
    }

    public enum PageKind {
        TEXT,
        CONCEALED_TEXT,
        IMAGE,
        ASPECTS,
        CRAFTING,
        ARCANE,
        CRUCIBLE,
        INFUSION,
        CREATURE_INFUSION
    }

    public record PageSpec(PageKind kind, String textKey, String concealedResearch, Identifier image, List<String> recipeKeys, String payload) {
        public PageSpec {
            recipeKeys = List.copyOf(recipeKeys);
        }

        public static PageSpec text(String textKey) {
            return new PageSpec(PageKind.TEXT, textKey, null, null, List.of(), null);
        }

        public static PageSpec concealedText(String concealedResearch, String textKey) {
            return new PageSpec(PageKind.CONCEALED_TEXT, textKey, concealedResearch, null, List.of(), null);
        }

        public static PageSpec image(Identifier image, String captionKey) {
            return new PageSpec(PageKind.IMAGE, captionKey, null, image, List.of(), null);
        }

        public static PageSpec aspects() {
            return new PageSpec(PageKind.ASPECTS, null, null, null, List.of(), null);
        }

        public static PageSpec recipes(PageKind kind, String... recipeKeys) {
            return new PageSpec(kind, null, null, null, List.of(recipeKeys), null);
        }

        public static PageSpec creatureInfusion(String payload) {
            return new PageSpec(PageKind.CREATURE_INFUSION, null, null, null, List.of(), payload);
        }
    }
}
