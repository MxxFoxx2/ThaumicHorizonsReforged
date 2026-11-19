package dev.visfox.thaumichorizons.world.pocket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PocketPlanesIndexTest {
    @Test
    void planeIndexUses256BlockSlices() {
        assertEquals(0, PocketPlanes.planeIndexAt(-128));
        assertEquals(0, PocketPlanes.planeIndexAt(0));
        assertEquals(0, PocketPlanes.planeIndexAt(127));
        assertEquals(1, PocketPlanes.planeIndexAt(128));
        assertEquals(1, PocketPlanes.planeIndexAt(383));
        assertEquals(2, PocketPlanes.planeIndexAt(384));
    }
}
