package dev.visfox.thaumichorizons.world.pocket;

import dev.visfox.thaumichorizons.lib.TagInput;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.biome.Biome;
import javax.annotation.Nullable;

public class PocketPlaneData {
    public int radius;
    public int color;
    public @Nullable int[] potionEffects;
    public @Nullable int[] portalA;
    public @Nullable int[] portalB;
    public @Nullable int[] portalC;
    public @Nullable int[] portalD;
    public String portalADimension = "";
    public String portalBDimension = "";
    public String portalCDimension = "";
    public String portalDDimension = "";
    public String sourceDimension = "";
    public String name = "Generic Pocket Plane";
    public @Nullable ResourceKey<Biome> biome;

    public PocketPlaneData() {
        this.radius = 32;
        this.color = 0;
    }

    public CompoundTag write(CompoundTag tag) {
        tag.putInt("radius", radius);
        if (potionEffects != null) {
            tag.putIntArray("effects", potionEffects);
        }
        tag.putString("name", name);
        tag.putInt("color", color);
        if (portalA != null) {
            tag.putIntArray("portalA", portalA);
        }
        if (portalB != null) {
            tag.putIntArray("portalB", portalB);
        }
        if (portalC != null) {
            tag.putIntArray("portalC", portalC);
        }
        if (portalD != null) {
            tag.putIntArray("portalD", portalD);
        }
        tag.putString("portalADimension", portalADimension);
        tag.putString("portalBDimension", portalBDimension);
        tag.putString("portalCDimension", portalCDimension);
        tag.putString("portalDDimension", portalDDimension);
        tag.putString("sourceDimension", sourceDimension);
        return tag;
    }

    public static PocketPlaneData read(CompoundTag tag) {
        PocketPlaneData data = new PocketPlaneData();
        int storedRadius = TagInput.getIntOr(tag, "radius", 32);
        // Generated planes are constrained to radius 32..127 so their
        // 256-block Z slices cannot overlap. Repair invalid persisted values
        // before portal placement or teardown uses the radius.
        data.radius = Math.max(32, Math.min(127, storedRadius));
        if (storedRadius != data.radius) {
            ThaumicHorizons.LOGGER.warn(
                "Clamping invalid Pocket Plane radius {} to {} while loading SavedData",
                storedRadius, data.radius);
        }
        data.potionEffects = TagInput.getIntArrayOr(tag, "effects");
        data.name = TagInput.getStringOr(tag, "name", "Generic Pocket Plane");
        data.color = TagInput.getIntOr(tag, "color", 0) & 0xFFFFFF;
        data.portalA = TagInput.getIntArrayOr(tag, "portalA");
        data.portalB = TagInput.getIntArrayOr(tag, "portalB");
        data.portalC = TagInput.getIntArrayOr(tag, "portalC");
        data.portalD = TagInput.getIntArrayOr(tag, "portalD");
        data.portalADimension = TagInput.getStringOr(tag, "portalADimension", "");
        data.portalBDimension = TagInput.getStringOr(tag, "portalBDimension", "");
        data.portalCDimension = TagInput.getStringOr(tag, "portalCDimension", "");
        data.portalDDimension = TagInput.getStringOr(tag, "portalDDimension", "");
        data.sourceDimension = TagInput.getStringOr(tag, "sourceDimension", "");
        return data;
    }

    public @Nullable int[] portal(int which) {
        return switch (which) {
            case 1 -> portalA;
            case 2 -> portalB;
            case 3 -> portalC;
            case 4 -> portalD;
            default -> null;
        };
    }

    public String portalDimension(int which) {
        return switch (which) {
            case 1 -> portalADimension;
            case 2 -> portalBDimension;
            case 3 -> portalCDimension;
            case 4 -> portalDDimension;
            default -> "";
        };
    }

    public void setPortal(int which, int x, int y, int z, String dimension, boolean xAligned) {
        int[] position = new int[] { x, y, z, xAligned ? 1 : 0 };
        switch (which) {
            case 1 -> {
                portalA = position;
                portalADimension = dimension;
            }
            case 2 -> {
                portalB = position;
                portalBDimension = dimension;
            }
            case 3 -> {
                portalC = position;
                portalCDimension = dimension;
            }
            case 4 -> {
                portalD = position;
                portalDDimension = dimension;
            }
            default -> throw new IllegalArgumentException("Unknown pocket portal: " + which);
        }
    }

    public void clearPortal(int which) {
        switch (which) {
            case 1 -> {
                portalA = null;
                portalADimension = "";
            }
            case 2 -> {
                portalB = null;
                portalBDimension = "";
            }
            case 3 -> {
                portalC = null;
                portalCDimension = "";
            }
            case 4 -> {
                portalD = null;
                portalDDimension = "";
            }
            default -> throw new IllegalArgumentException("Unknown pocket portal: " + which);
        }
    }

    public @Nullable MobEffect potionEffect(int index) {
        if (potionEffects == null || index < 0 || index >= potionEffects.length) {
            return null;
        }
        return BuiltInRegistries.MOB_EFFECT.byId(potionEffects[index]);
    }
}
