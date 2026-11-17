package dev.visfox.thaumichorizons.world.pocket;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import org.jspecify.annotations.Nullable;

public class PocketPlaneData {
    public int radius;
    public int color;
    public @Nullable int[] potionEffects;
    public @Nullable int[] portalA;
    public @Nullable int[] portalB;
    public @Nullable int[] portalC;
    public @Nullable int[] portalD;
    public String name = "Generic Pocket Plane";

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
        return tag;
    }

    public static PocketPlaneData read(CompoundTag tag, HolderLookup.Provider registries) {
        PocketPlaneData data = new PocketPlaneData();
        data.radius = tag.getIntOr("radius", 32);
        data.potionEffects = tag.getIntArray("effects").orElse(null);
        data.name = tag.getStringOr("name", "Generic Pocket Plane");
        data.color = tag.getIntOr("color", 0);
        data.portalA = tag.getIntArray("portalA").orElse(null);
        data.portalB = tag.getIntArray("portalB").orElse(null);
        data.portalC = tag.getIntArray("portalC").orElse(null);
        data.portalD = tag.getIntArray("portalD").orElse(null);
        return data;
    }

    public int[] portal(int which) {
        return switch (which) {
            case 1 -> portalA;
            case 2 -> portalB;
            case 3 -> portalC;
            case 4 -> portalD;
            default -> null;
        };
    }

    public @Nullable MobEffect potionEffect(int index) {
        if (potionEffects == null || index < 0 || index >= potionEffects.length) {
            return null;
        }
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.getValue(potionEffects[index]);
        return effect == null ? null : effect;
    }
}
