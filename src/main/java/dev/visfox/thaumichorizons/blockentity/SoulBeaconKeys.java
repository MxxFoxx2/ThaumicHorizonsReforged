package dev.visfox.thaumichorizons.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.visfox.thaumichorizons.platform.DataKey;
import net.minecraft.core.BlockPos;

public final class SoulBeaconKeys {
    public record SoulBeaconData(boolean set, int x, int y, int z, String dimension) {
        public static final SoulBeaconData EMPTY = new SoulBeaconData(false, 0, 0, 0, "");

        public static final Codec<SoulBeaconData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("set", false).forGetter(SoulBeaconData::set),
            Codec.INT.optionalFieldOf("x", 0).forGetter(SoulBeaconData::x),
            Codec.INT.optionalFieldOf("y", 0).forGetter(SoulBeaconData::y),
            Codec.INT.optionalFieldOf("z", 0).forGetter(SoulBeaconData::z),
            Codec.STRING.optionalFieldOf("dimension", "").forGetter(SoulBeaconData::dimension)
        ).apply(instance, SoulBeaconData::new));

        public BlockPos pos() {
            return new BlockPos(x, y, z);
        }
    }

    public static final DataKey<SoulBeaconData> DATA = DataKey.builder("soul_beacon", () -> SoulBeaconData.EMPTY)
        .codec(SoulBeaconData.CODEC)
        .build();

    public static void initialize() {
        java.util.Objects.requireNonNull(DATA);
    }

    private SoulBeaconKeys() {
    }
}
