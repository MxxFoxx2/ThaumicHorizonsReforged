package dev.visfox.thaumichorizons.world.pocket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

class PocketPlaneDataTest {
    @Test
    void radiusBelowMinimumIsClampedTo32() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("radius", 5);
        assertEquals(32, PocketPlaneData.read(tag).radius);
    }

    @Test
    void radiusAboveMaximumIsClampedTo127() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("radius", 500);
        assertEquals(127, PocketPlaneData.read(tag).radius);
    }

    @Test
    void validRadiusIsPreserved() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("radius", 64);
        assertEquals(64, PocketPlaneData.read(tag).radius);
    }

    @Test
    void missingFieldsUseLegacyDefaults() {
        PocketPlaneData data = PocketPlaneData.read(new CompoundTag());
        assertEquals(32, data.radius);
        assertEquals("Generic Pocket Plane", data.name);
        assertEquals(0, data.color);
        assertNull(data.portal(1));
    }

    @Test
    void colorIsMaskedTo24Bits() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", 0x01ABCDEF);
        assertEquals(0xABCDEF, PocketPlaneData.read(tag).color);
    }

    @Test
    void portalRecordsReadAndIndex() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("portalC", new int[] { 1, 2, 3, 0 });
        tag.putString("portalCDimension", "overworld");
        PocketPlaneData data = PocketPlaneData.read(tag);
        assertArrayEquals(new int[] { 1, 2, 3, 0 }, data.portal(3));
        assertEquals("overworld", data.portalDimension(3));
        assertNull(data.portal(0));
    }

    @Test
    void setPortalAndClearPortalRoundTrip() {
        PocketPlaneData data = new PocketPlaneData();
        data.setPortal(2, 10, 20, 30, "the_nether", true);
        assertArrayEquals(new int[] { 10, 20, 30, 1 }, data.portal(2));
        assertEquals("the_nether", data.portalDimension(2));
        data.clearPortal(2);
        assertNull(data.portal(2));
        assertTrue(data.portalDimension(2).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> data.clearPortal(9));
    }

    @Test
    void writeReadRoundTripKeepsIdentity() {
        PocketPlaneData original = new PocketPlaneData();
        original.radius = 96;
        original.color = 0x123456;
        original.name = "Plane";
        original.sourceDimension = "minecraft:overworld";
        original.setPortal(1, 4, 5, 6, "minecraft:the_nether", false);
        PocketPlaneData restored = PocketPlaneData.read(original.write(new CompoundTag()));
        assertEquals(96, restored.radius);
        assertEquals(0x123456, restored.color);
        assertEquals("Plane", restored.name);
        assertEquals("minecraft:overworld", restored.sourceDimension);
        assertArrayEquals(new int[] { 4, 5, 6, 0 }, restored.portal(1));
        assertEquals("minecraft:the_nether", restored.portalDimension(1));
    }
}
