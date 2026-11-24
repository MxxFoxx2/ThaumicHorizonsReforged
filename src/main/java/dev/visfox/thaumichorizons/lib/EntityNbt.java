package dev.visfox.thaumichorizons.lib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public final class EntityNbt {
    private EntityNbt() {
    }

    public static CompoundTag save(Entity entity) {
        CompoundTag tag = entity.saveWithoutId(new CompoundTag());
        tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        return tag;
    }

    public static @Nullable Entity load(CompoundTag tag, Level level) {
        return EntityType.loadEntityRecursive(tag, level, entity -> entity);
    }
}
