package dev.visfox.thaumichorizons.research;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import thaumcraft.research.ResearchCategories;
import thaumcraft.research.ResearchCategory;
import thaumcraft.research.ResearchItem;
import thaumcraft.research.ResearchPage;

class ConfigResearchRegistrationTest {
    private static final String CATEGORY = "ThaumicHorizons";
    private static final int EXPECTED_ENTRIES = 74;

    @BeforeAll
    static void registerThResearchOnce() {
        if (ResearchCategories.get(CATEGORY) == null) {
            ConfigResearch.init();
        }
    }

    @Test
    void thCategoryIsRegistered() {
        ResearchCategory category = ResearchCategories.get(CATEGORY);
        assertNotNull(category);
        assertEquals("ThaumicHorizons", category.key());
    }

    @Test
    void allThEntriesAreRegistered() {
        ResearchCategory category = ResearchCategories.get(CATEGORY);
        assertNotNull(category);
        List<ResearchItem> entries = List.copyOf(category.research());
        assertEquals(EXPECTED_ENTRIES, entries.size());
        for (ResearchItem entry : entries) {
            assertEquals(CATEGORY, entry.category());
            assertTrue(entry.pages().size() > 0, "entry " + entry.key() + " has no pages");
        }
    }

    @Test
    void voidPuttyHasRepairRecipePage() {
        ResearchItem entry = ResearchCategories.getResearch("voidPutty");
        assertNotNull(entry);
        assertEquals(2, entry.pages().size());
        ResearchPage page = entry.pages().get(1);
        assertTrue(page instanceof ResearchPage.Text);
        assertEquals("voidPutty2", ((ResearchPage.Text) page).text());
    }

    @Test
    void creatureAndSelfInfusionPagesUseStandardImageMarkers() {
        ResearchCategory category = ResearchCategories.get(CATEGORY);
        assertNotNull(category);
        long markerCount = category.research().stream()
            .flatMap(entry -> entry.pages().stream())
            .filter(page -> page instanceof ResearchPage.Image image
                && (image.image().getPath().startsWith("textures/misc/creature_page/")
                    || image.image().getPath().startsWith("textures/misc/self_page/")))
            .count();
        assertEquals(40L, markerCount);
    }

    @Test
    void everyInfusionPageEntryListsInfusionAsHiddenParent() {
        ResearchCategory category = ResearchCategories.get(CATEGORY);
        assertNotNull(category);
        List<String> offenders = new java.util.ArrayList<>();
        for (ResearchItem entry : category.research()) {
            boolean hasInfusionPage = entry.pages().stream().anyMatch(page -> page instanceof ResearchPage.Infusion);
            if (hasInfusionPage && !entry.parentsHidden().contains("INFUSION")) {
                offenders.add(entry.key() + " (parents=" + entry.parents() + ", hidden=" + entry.parentsHidden() + ")");
            }
        }
        assertTrue(offenders.isEmpty(),
            "entries with infusion pages missing hidden INFUSION parent: " + offenders);
    }
}
