package dev.visfox.thaumichorizons.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SyringeKeysTest {

    @Test
    void effectDataDefaultsToFalse() {
        CompoundTag tag = new CompoundTag();
        assertEquals(new SyringeKeys.InjectionData(false),
            SyringeKeys.InjectionData.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow());
        assertEquals(new SyringeKeys.BloodData(false),
            SyringeKeys.BloodData.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow());
    }

    @Test
    void effectDataRoundTripsPhialFlag() {
        SyringeKeys.InjectionData injection = new SyringeKeys.InjectionData(true);
        SyringeKeys.BloodData blood = new SyringeKeys.BloodData(true);

        CompoundTag encodedInjection = (CompoundTag) SyringeKeys.InjectionData.CODEC
            .encodeStart(NbtOps.INSTANCE, injection).getOrThrow();
        CompoundTag encodedBlood = (CompoundTag) SyringeKeys.BloodData.CODEC
            .encodeStart(NbtOps.INSTANCE, blood).getOrThrow();

        assertTrue(SyringeKeys.InjectionData.CODEC.parse(NbtOps.INSTANCE, encodedInjection).getOrThrow().phial());
        assertTrue(SyringeKeys.BloodData.CODEC.parse(NbtOps.INSTANCE, encodedBlood).getOrThrow().phial());
    }

    @Test
    void effectDataStreamCodecsRoundTripPhialFlag() {
        ByteBuf injectionBuffer = Unpooled.buffer();
        ByteBuf bloodBuffer = Unpooled.buffer();
        SyringeKeys.InjectionData injection = new SyringeKeys.InjectionData(true);
        SyringeKeys.BloodData blood = new SyringeKeys.BloodData(false);

        SyringeKeys.InjectionData.STREAM_CODEC.encode(injectionBuffer, injection);
        SyringeKeys.BloodData.STREAM_CODEC.encode(bloodBuffer, blood);

        assertEquals(injection, SyringeKeys.InjectionData.STREAM_CODEC.decode(injectionBuffer));
        assertEquals(blood, SyringeKeys.BloodData.STREAM_CODEC.decode(bloodBuffer));
        injectionBuffer.release();
        bloodBuffer.release();
    }

    @Test
    void sampleDataUsesDefaultsFromEmptyTag() {
        assertEquals(SyringeKeys.SampleData.EMPTY,
            SyringeKeys.SampleData.CODEC.parse(NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
    }

    @Test
    void sampleDataRoundTripsAllFields() {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:cow");
        SyringeKeys.SampleData sample = new SyringeKeys.SampleData(entity, "Cow", List.of(1, 2, 7));

        CompoundTag encoded = (CompoundTag) SyringeKeys.SampleData.CODEC
            .encodeStart(NbtOps.INSTANCE, sample).getOrThrow();
        SyringeKeys.SampleData decoded = SyringeKeys.SampleData.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow();

        assertEquals(sample, decoded);
    }

    @Test
    void sampleDataStreamCodecRoundTripsAllFields() {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:sheep");
        SyringeKeys.SampleData sample = new SyringeKeys.SampleData(entity, "Sheep", List.of(3, 4));
        ByteBuf buffer = Unpooled.buffer();

        SyringeKeys.SampleData.STREAM_CODEC.encode(buffer, sample);
        SyringeKeys.SampleData decoded = SyringeKeys.SampleData.STREAM_CODEC.decode(buffer);

        assertEquals(sample, decoded);
        buffer.release();
    }

    @Test
    @Disabled("Requires NeoForge LoadingModList; covered by dedicated-server runtime smoke")
    void ammoDataUsesEmptyListAsDefault() {
        assertEquals(SyringeKeys.AmmoData.EMPTY,
            SyringeKeys.AmmoData.CODEC.parse(NbtOps.INSTANCE, new ListTag()).getOrThrow());
    }
}
