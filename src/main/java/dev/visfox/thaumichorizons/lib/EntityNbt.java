package dev.visfox.thaumichorizons.lib;

import net.minecraft.util.ProblemReporter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.Nullable;

public final class EntityNbt {
    private EntityNbt() {
    }

    public static CompoundTag save(Entity entity) {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        entity.saveWithoutId(output);
        CompoundTag tag = output.buildResult();
        tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        return tag;
    }

    public static @Nullable Entity load(CompoundTag tag, Level level) {
        return EntityType.loadEntityRecursive(tag, level, EntitySpawnReason.LOAD, EntityProcessor.NOP);
    }
}
