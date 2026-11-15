package dev.visfox.thaumichorizons.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.visfox.thaumichorizons.platform.StackKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class SyringeKeys {
    public record InjectionData(boolean phial) {
        public static final Codec<InjectionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("phial", false).forGetter(InjectionData::phial)
        ).apply(instance, InjectionData::new));

        public static final StreamCodec<ByteBuf, InjectionData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, InjectionData::phial,
            InjectionData::new);
    }

    public record BloodData(boolean phial) {
        public static final Codec<BloodData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("phial", false).forGetter(BloodData::phial)
        ).apply(instance, BloodData::new));

        public static final StreamCodec<ByteBuf, BloodData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, BloodData::phial,
            BloodData::new);
    }

    public record AmmoData(List<ItemStack> ammo) {
        public static final AmmoData EMPTY = new AmmoData(new ArrayList<>());

        public AmmoData {
            ammo = List.copyOf(ammo);
        }

        public static final Codec<AmmoData> CODEC = ItemStack.CODEC.listOf()
            .xmap(AmmoData::new, AmmoData::ammo);

        public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AmmoData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);
    }

    public static final StackKey<InjectionData> INJECTION = StackKey.register("syringe_injection", InjectionData.CODEC, InjectionData.STREAM_CODEC);
    public static final StackKey<BloodData> BLOOD = StackKey.register("syringe_blood", BloodData.CODEC, BloodData.STREAM_CODEC);
    public static final StackKey<AmmoData> AMMO = StackKey.register("injector_ammo", AmmoData.CODEC, AmmoData.STREAM_CODEC);

    public record SampleData(CompoundTag entity, String critterName, List<Integer> infusions) {
        public static final SampleData EMPTY = new SampleData(new CompoundTag(), "", List.of());

        public static final Codec<SampleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.optionalFieldOf("entity", new CompoundTag()).forGetter(SampleData::entity),
            Codec.STRING.optionalFieldOf("critter_name", "").forGetter(SampleData::critterName),
            Codec.INT.listOf().optionalFieldOf("infusions", List.of()).forGetter(SampleData::infusions)
        ).apply(instance, SampleData::new));

        public static final StreamCodec<ByteBuf, SampleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, SampleData::entity,
            ByteBufCodecs.STRING_UTF8, SampleData::critterName,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), SampleData::infusions,
            SampleData::new);
    }

    public static final StackKey<SampleData> SAMPLE = StackKey.register("syringe_sample", SampleData.CODEC, SampleData.STREAM_CODEC);

    public static void init() {
    }

    private SyringeKeys() {
    }
}
