package dev.visfox.thaumichorizons.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.visfox.thaumichorizons.platform.StackKey;
import java.util.Objects;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.nbt.CompoundTag;

public final class SoulJarKeys {
    public record SoulJarData(CompoundTag entity, boolean soul, String critterName, String villagerType) {
        public static final SoulJarData EMPTY = new SoulJarData(new CompoundTag(), false, "", "");

        public static final Codec<SoulJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.optionalFieldOf("entity", new CompoundTag()).forGetter(SoulJarData::entity),
            Codec.BOOL.optionalFieldOf("soul", false).forGetter(SoulJarData::soul),
            Codec.STRING.optionalFieldOf("critter_name", "").forGetter(SoulJarData::critterName),
            Codec.STRING.optionalFieldOf("villager_type", "").forGetter(SoulJarData::villagerType)
        ).apply(instance, SoulJarData::new));

        public static final StreamCodec<ByteBuf, SoulJarData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, SoulJarData::entity,
            ByteBufCodecs.BOOL, SoulJarData::soul,
            ByteBufCodecs.STRING_UTF8, SoulJarData::critterName,
            ByteBufCodecs.STRING_UTF8, SoulJarData::villagerType,
            SoulJarData::new);
    }

    public static final StackKey<SoulJarData> DATA = StackKey.register("soul_jar", SoulJarData.CODEC, SoulJarData.STREAM_CODEC);

    public static void initialize() {
        Objects.requireNonNull(DATA);
    }

    private SoulJarKeys() {
    }
}
