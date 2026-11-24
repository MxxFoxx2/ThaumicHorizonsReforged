package dev.visfox.thaumichorizons.lib;

import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public final class TagInput {
    private TagInput() {
    }

    public static int getIntOr(CompoundTag tag, String key, int fallback) {
        return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getInt(key) : fallback;
    }

    public static long getLongOr(CompoundTag tag, String key, long fallback) {
        return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getLong(key) : fallback;
    }

    public static boolean getBooleanOr(CompoundTag tag, String key, boolean fallback) {
        return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getBoolean(key) : fallback;
    }

    public static double getDoubleOr(CompoundTag tag, String key, double fallback) {
        return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getDouble(key) : fallback;
    }

    public static String getStringOr(CompoundTag tag, String key, String fallback) {
        return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : fallback;
    }

    public static int[] getIntArrayOr(CompoundTag tag, String key) {
        return tag.contains(key, Tag.TAG_INT_ARRAY) ? tag.getIntArray(key) : null;
    }

    public static ListTag getListOrEmpty(CompoundTag tag, String key) {
        return tag.contains(key, Tag.TAG_LIST) ? tag.getList(key, Tag.TAG_COMPOUND) : new ListTag();
    }

    public static CompoundTag getCompoundOrEmpty(CompoundTag tag, String key) {
        return tag.contains(key, Tag.TAG_COMPOUND) ? tag.getCompound(key) : new CompoundTag();
    }

    public static CompoundTag getCompoundOrEmpty(ListTag list, int index) {
        return index >= 0 && index < list.size() ? list.getCompound(index) : new CompoundTag();
    }

    public static Set<String> keySet(CompoundTag tag) {
        return tag.getAllKeys();
    }
}
