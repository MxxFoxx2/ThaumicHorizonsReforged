package dev.visfox.thaumichorizons.world.pocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import org.junit.jupiter.api.Test;

class PocketPlanesSavedDataTest {
    @Test
    void inconsistentMaxIdIsRecoveredFromSavedPlanes() {
        CompoundTag tag = new CompoundTag();
        tag.put("Data", twoPlanes());
        tag.putInt("MaxID", 9);
        load(tag);
        assertEquals(2, PocketPlanes.planes.size());
        assertEquals(2, PocketPlanes.pocketPlaneMAXID);
    }

    @Test
    void malformedReturnPositionsAreIgnored() {
        CompoundTag tag = new CompoundTag();
        tag.put("Data", twoPlanes());
        CompoundTag positions = new CompoundTag();
        positions.putIntArray("0", new int[] { 10, 20, 30 });
        positions.putIntArray("abc", new int[] { 1, 2, 3 });
        positions.putIntArray("-1", new int[] { 1, 2, 3 });
        positions.putIntArray("9", new int[] { 1, 2, 3 });
        positions.putIntArray("1", new int[] { 1, 2 });
        tag.put("Positions", positions);
        load(tag);
        assertEquals(1, PocketPlanes.positions.size());
        assertEquals(new BlockPos(10, 20, 30), PocketPlanes.positions.get(0));
        assertNull(PocketPlanes.positions.get(1));
        assertNull(PocketPlanes.positions.get(9));
        assertFalse(PocketPlanes.positions.containsKey(-1));
    }

    @Test
    void loadingAnotherSaveClearsPreviousPlaneState() {
        CompoundTag first = new CompoundTag();
        first.put("Data", twoPlanes());
        CompoundTag positions = new CompoundTag();
        positions.putIntArray("0", new int[] { 1, 2, 3 });
        first.put("Positions", positions);
        load(first);
        assertEquals(2, PocketPlanes.planes.size());
        assertEquals(1, PocketPlanes.positions.size());

        load(new CompoundTag());
        assertTrue(PocketPlanes.planes.isEmpty());
        assertTrue(PocketPlanes.positions.isEmpty());
        assertEquals(0, PocketPlanes.pocketPlaneMAXID);
    }

    @Test
    void invalidRadiusIsClampedOnLoad() {
        PocketPlaneData plane = new PocketPlaneData();
        plane.radius = 500;
        CompoundTag tag = new CompoundTag();
        ListTag data = new ListTag();
        data.add(plane.write(new CompoundTag()));
        tag.put("Data", data);
        load(tag);
        assertEquals(127, PocketPlanes.planes.get(0).radius);
    }

    @Test
    void missingPortalRecordIsFree() {
        assertEquals(1, firstAvailableAfterLoad(new PocketPlaneData()));
    }

    @Test
    void threeIntPortalRecordIsFree() {
        PocketPlaneData plane = new PocketPlaneData();
        plane.portalA = new int[] { 1, 2, 3 };
        plane.portalADimension = "minecraft:overworld";
        assertEquals(1, firstAvailableAfterLoad(plane));
    }

    @Test
    void invalidDimensionInPortalRecordIsFree() {
        PocketPlaneData plane = new PocketPlaneData();
        plane.setPortal(1, 1, 2, 3, "", false);
        assertEquals(1, firstAvailableAfterLoad(plane));
    }

    @Test
    void invalidOrientationInPortalRecordIsFree() {
        PocketPlaneData plane = new PocketPlaneData();
        plane.portalA = new int[] { 1, 2, 3, 7 };
        plane.portalADimension = "minecraft:overworld";
        assertEquals(1, firstAvailableAfterLoad(plane));
    }

    @Test
    void zeroCoordinatePortalRecordIsFree() {
        PocketPlaneData plane = new PocketPlaneData();
        plane.setPortal(1, 0, 0, 0, "minecraft:overworld", false);
        assertEquals(1, firstAvailableAfterLoad(plane));
    }

    @Test
    void validPortalRecordOccupiesItsSlot() {
        PocketPlaneData plane = new PocketPlaneData();
        plane.setPortal(1, 1, 2, 3, "minecraft:overworld", true);
        assertEquals(2, firstAvailableAfterLoad(plane));
    }

    @Test
    void fourValidPortalRecordsLeaveNoFreeSlot() {
        PocketPlaneData plane = new PocketPlaneData();
        for (int which = 1; which <= 4; ++which) {
            plane.setPortal(which, which, 2, 3, "minecraft:overworld", false);
        }
        assertEquals(0, firstAvailableAfterLoad(plane));
    }

    private static ListTag twoPlanes() {
        ListTag data = new ListTag();
        data.add(new PocketPlaneData().write(new CompoundTag()));
        data.add(new PocketPlaneData().write(new CompoundTag()));
        return data;
    }

    private static int firstAvailableAfterLoad(PocketPlaneData plane) {
        CompoundTag tag = new CompoundTag();
        ListTag data = new ListTag();
        data.add(plane.write(new CompoundTag()));
        tag.put("Data", data);
        load(tag);
        return PocketPlanes.firstAvailablePortal(0);
    }

    private static void load(CompoundTag tag) {
        PocketPlanes.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }
}
