package dev.visfox.thaumichorizons.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GolemMaterialTHTest {
    @Test
    void allSixteenMaterialsExistWithStableIds() {
        assertEquals(16, GolemMaterialTH.values().length);
        for (GolemMaterialTH material : GolemMaterialTH.values()) {
            assertEquals(material.name().toLowerCase(java.util.Locale.ROOT), material.id());
            assertSame(material, GolemMaterialTH.byId(material.id()));
        }
        assertEquals(16, java.util.Arrays.stream(GolemMaterialTH.values()).map(GolemMaterialTH::id).distinct().count());
    }

    @Test
    void statsMatchTheOriginalEnum() {
        assertStats(GolemMaterialTH.GRASS, 10, 0, 0.38F, false, 2, 1, 75, 0, 750);
        assertStats(GolemMaterialTH.DIRT, 10, 0, 0.38F, false, 1, 1, 75, 0, 500);
        assertStats(GolemMaterialTH.WOOD, 20, 6, 0.35F, false, 2, 4, 75, 1, 750);
        assertStats(GolemMaterialTH.PLANT, 10, 0, 0.40F, false, 2, 1, 75, 0, 750);
        assertStats(GolemMaterialTH.ROCK, 30, 12, 0.32F, true, 2, 16, 100, 3, 1000);
        assertStats(GolemMaterialTH.METAL, 35, 15, 0.31F, true, 2, 32, 125, 4, 1250);
        assertStats(GolemMaterialTH.CLOTH, 10, 0, 0.40F, false, 2, 1, 75, 0, 750);
        assertStats(GolemMaterialTH.SAND, 10, 0, 0.40F, false, 1, 1, 75, 0, 500);
        assertStats(GolemMaterialTH.REDSTONE, 20, 9, 0.35F, true, 2, 8, 45, 2, 1000);
        assertStats(GolemMaterialTH.TNT, 10, 0, 0.38F, false, 1, 1, 75, 0, 750);
        assertStats(GolemMaterialTH.ICE, 20, 6, 0.35F, false, 2, 4, 75, 1, 750);
        assertStats(GolemMaterialTH.CACTUS, 20, 6, 0.35F, false, 2, 4, 75, 1, 1000);
        assertStats(GolemMaterialTH.CLAY, 25, 9, 0.33F, true, 1, 8, 100, 2, 750);
        assertStats(GolemMaterialTH.CAKE, 20, 6, 0.35F, false, 2, 4, 25, 1, 750);
        assertStats(GolemMaterialTH.WEB, 15, 6, 0.35F, false, 2, 4, 40, 1, 1000);
        assertStats(GolemMaterialTH.VOID, 25, 15, 0.40F, true, 2, 8, 20, 3, 0);
    }

    private static void assertStats(GolemMaterialTH material, int health, int armor, float speed, boolean fireResist,
        int upgrades, int carry, int regenDelay, int strength, int visCost) {
        assertEquals(health, material.health, material.id() + " health");
        assertEquals(armor, material.armor, material.id() + " armor");
        assertEquals(speed, material.speed, material.id() + " speed");
        assertEquals(fireResist, material.fireResist, material.id() + " fireResist");
        assertEquals(upgrades, material.upgrades, material.id() + " upgrades");
        assertEquals(carry, material.carry, material.id() + " carry");
        assertEquals(regenDelay, material.regenDelay, material.id() + " regenDelay");
        assertEquals(strength, material.strength, material.id() + " strength");
        assertEquals(visCost, material.visCost, material.id() + " visCost");
        assertEquals(health, material.stats().health(), material.id() + " stats.health");
        assertEquals(upgrades, material.stats().upgrades(), material.id() + " stats.upgrades");
        assertEquals(visCost, material.stats().visCost(), material.id() + " stats.visCost");
    }

    @Test
    void thWoodHasTwoUpgradeSlotsUnlikeStockWood() {
        assertEquals(2, GolemMaterialTH.WOOD.upgrades);
    }

    @Test
    void unknownIdentifierFallsBackToVoid() {
        assertSame(GolemMaterialTH.VOID, GolemMaterialTH.byId("bogus"));
        assertSame(GolemMaterialTH.VOID, GolemMaterialTH.byId(""));
        assertSame(GolemMaterialTH.VOID, GolemMaterialTH.byId(null));
    }

    @Test
    void legacyOrdinalsRestoreMaterials() {
        assertSame(GolemMaterialTH.GRASS, GolemMaterialTH.legacy(8));
        assertSame(GolemMaterialTH.DIRT, GolemMaterialTH.legacy(9));
        assertSame(GolemMaterialTH.PLANT, GolemMaterialTH.legacy(10));
        assertSame(GolemMaterialTH.ROCK, GolemMaterialTH.legacy(11));
        assertSame(GolemMaterialTH.METAL, GolemMaterialTH.legacy(12));
        assertSame(GolemMaterialTH.CLOTH, GolemMaterialTH.legacy(13));
        assertSame(GolemMaterialTH.SAND, GolemMaterialTH.legacy(14));
        assertSame(GolemMaterialTH.REDSTONE, GolemMaterialTH.legacy(15));
        assertSame(GolemMaterialTH.TNT, GolemMaterialTH.legacy(16));
        assertSame(GolemMaterialTH.ICE, GolemMaterialTH.legacy(17));
        assertSame(GolemMaterialTH.CACTUS, GolemMaterialTH.legacy(18));
        assertSame(GolemMaterialTH.CAKE, GolemMaterialTH.legacy(19));
        assertSame(GolemMaterialTH.WEB, GolemMaterialTH.legacy(20));
        assertSame(GolemMaterialTH.VOID, GolemMaterialTH.legacy(21));
        assertSame(GolemMaterialTH.WOOD, GolemMaterialTH.legacy(1));
        assertSame(GolemMaterialTH.CLAY, GolemMaterialTH.legacy(3));
        assertSame(GolemMaterialTH.ROCK, GolemMaterialTH.legacy(5));
        assertSame(GolemMaterialTH.METAL, GolemMaterialTH.legacy(6));
        assertNull(GolemMaterialTH.legacy(999));
        assertNull(GolemMaterialTH.legacy(0));
    }

    @Test
    void fromBlockNullIsVoid() {
        assertSame(GolemMaterialTH.VOID, GolemMaterialTH.fromBlock(null));
    }

    @Test
    void allMaterialsKeepCurrentWaterBehavior() {
        for (GolemMaterialTH material : GolemMaterialTH.values()) {
            assertTrue(material.stats().heavy(), material.id() + " should keep the current water behavior");
        }
    }
}
