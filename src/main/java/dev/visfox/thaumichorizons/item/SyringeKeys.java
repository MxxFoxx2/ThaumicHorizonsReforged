package dev.visfox.thaumichorizons.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.visfox.thaumichorizons.platform.StackKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    /** Client-visible draw and rotating-cylinder state for the legacy injector model. */
    public record InjectorVisualData(int useTicks, int rotation, int rotationTarget, int rotationStartedAt) {
        public static final InjectorVisualData EMPTY = new InjectorVisualData(0, 0, 0, 0);

        public static final Codec<InjectorVisualData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("use_ticks", 0).forGetter(InjectorVisualData::useTicks),
            Codec.INT.optionalFieldOf("rotation", 0).forGetter(InjectorVisualData::rotation),
            Codec.INT.optionalFieldOf("rotation_target", 0).forGetter(InjectorVisualData::rotationTarget),
            Codec.INT.optionalFieldOf("rotation_started_at", 0).forGetter(InjectorVisualData::rotationStartedAt)
        ).apply(instance, InjectorVisualData::new));

        public static final StreamCodec<ByteBuf, InjectorVisualData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, InjectorVisualData::useTicks,
            ByteBufCodecs.INT, InjectorVisualData::rotation,
            ByteBufCodecs.INT, InjectorVisualData::rotationTarget,
            ByteBufCodecs.INT, InjectorVisualData::rotationStartedAt,
            InjectorVisualData::new);

        public InjectorVisualData withUseTicks(int ticks) {
            return new InjectorVisualData(Math.max(0, ticks), rotation, rotationTarget, rotationStartedAt);
        }

        public float drawProgress() {
            float charge = Math.min(useTicks / 30.0F, 1.0F);
            return (charge * charge + charge * 2.0F) / 3.0F;
        }

        public float rotationAt(int gameTick) {
            int elapsed = Math.max(0, gameTick - rotationStartedAt);
            float progress = Math.min(elapsed / 10.0F, 1.0F);
            int delta = Math.floorMod(rotationTarget - rotation + 180, 360) - 180;
            return rotation + delta * progress;
        }
    }

    public static final StackKey<InjectorVisualData> INJECTOR_VISUAL =
        StackKey.register("injector_visual", InjectorVisualData.CODEC, InjectorVisualData.STREAM_CODEC);

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

    public static void initialize() {
        Objects.requireNonNull(INJECTION);
        Objects.requireNonNull(BLOOD);
        Objects.requireNonNull(AMMO);
        Objects.requireNonNull(INJECTOR_VISUAL);
        Objects.requireNonNull(SAMPLE);
    }

    private SyringeKeys() {
    }
}
