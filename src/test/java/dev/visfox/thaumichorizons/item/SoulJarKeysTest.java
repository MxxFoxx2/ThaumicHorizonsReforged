package dev.visfox.thaumichorizons.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.junit.jupiter.api.Test;

class SoulJarKeysTest {
    @Test
    void emptyTagUsesDefaults() {
        SoulJarKeys.SoulJarData data = SoulJarKeys.SoulJarData.CODEC
            .parse(NbtOps.INSTANCE, new CompoundTag()).getOrThrow();
        assertEquals(SoulJarKeys.SoulJarData.EMPTY, data);
    }

    @Test
    void partialTagUsesDefaults() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("soul", true);
        SoulJarKeys.SoulJarData data = SoulJarKeys.SoulJarData.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
        assertTrue(data.soul());
        assertFalse(data.entity().contains("id"));
        assertEquals("", data.critterName());
        assertEquals("", data.villagerType());
    }

    @Test
    void codecRoundTripPreservesAllFields() {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:pig");
        entity.putInt("Age", 3);
        CompoundTag tag = new CompoundTag();
        tag.put("entity", entity);
        tag.putBoolean("soul", true);
        tag.putString("critter_name", "Pig");
        tag.putString("villager_type", "minecraft:cleric");
        SoulJarKeys.SoulJarData data = SoulJarKeys.SoulJarData.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
        assertEquals(new SoulJarKeys.SoulJarData(entity, true, "Pig", "minecraft:cleric"), data);

        CompoundTag encoded = (CompoundTag) SoulJarKeys.SoulJarData.CODEC
            .encodeStart(NbtOps.INSTANCE, data).getOrThrow();

        assertEquals(data, SoulJarKeys.SoulJarData.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow());
    }

    @Test
    void streamCodecRoundTripPreservesAllFields() {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:villager");
        SoulJarKeys.SoulJarData data = new SoulJarKeys.SoulJarData(entity, true, "Villager", "minecraft:cleric");
        ByteBuf buffer = Unpooled.buffer();

        SoulJarKeys.SoulJarData.STREAM_CODEC.encode(buffer, data);
        SoulJarKeys.SoulJarData decoded = SoulJarKeys.SoulJarData.STREAM_CODEC.decode(buffer);

        assertEquals(data, decoded);
        buffer.release();
    }
}
