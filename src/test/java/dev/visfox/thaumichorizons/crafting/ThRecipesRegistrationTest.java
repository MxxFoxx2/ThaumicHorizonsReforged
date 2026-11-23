package dev.visfox.thaumichorizons.crafting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import thaumcraft.crafting.CompoundRecipe;
import thaumcraft.crafting.TcRecipe;
import thaumcraft.crafting.TcRecipes;

class ThRecipesRegistrationTest {
    private static final int EXPECTED_RECIPES = 62;

    @BeforeAll
    static void registerThRecipesOnce() {
        if (TcRecipes.get("cowInfusion") == null) {
            ThRecipes.init();
        }
    }

    @Test
    void thRecipesAreRegisteredIntoTheTcRegistry() {
        Set<String> keys = new HashSet<>();
        for (TcRecipe recipe : TcRecipes.all()) {
            assertTrue(keys.add(recipe.key()), "duplicate recipe key " + recipe.key());
        }
        assertEquals(EXPECTED_RECIPES, keys.size());
    }

    @Test
    void knownRecipesResolveByTheirKeys() {
        assertNotNull(TcRecipes.get("cowInfusion"));
        assertNotNull(TcRecipes.get("alcheponics"));
        assertNotNull(TcRecipes.get("meatGrowth"));
        assertNotNull(TcRecipes.get("fleshLeather"));
        assertNotNull(TcRecipes.get("CurativeVat"));
    }

    @Test
    void curativeVatStructureMatchesTheReferenceVolume() {
        CompoundRecipe recipe = (CompoundRecipe) TcRecipes.get("CurativeVat");
        assertNotNull(recipe);
        assertEquals(3, recipe.width());
        assertEquals(4, recipe.height());
        assertEquals(3, recipe.depth());
        assertEquals(36, recipe.blocks().size());
    }

    @Test
    void secondRegistrationPassFailsFastOnDuplicates() {
        assertThrows(IllegalStateException.class, ThRecipes::init);
    }
}
