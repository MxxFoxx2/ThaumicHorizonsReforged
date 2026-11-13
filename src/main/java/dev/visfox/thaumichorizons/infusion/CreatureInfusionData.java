package dev.visfox.thaumichorizons.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CreatureInfusionData(int[] infusions, boolean chameleon, boolean climb, int tumorWarp, int tumorWarpSticky, int tumorWarpTemp) {
    public static final int NUM_INFUSIONS = 12;

    public static final CreatureInfusionData EMPTY = new CreatureInfusionData(new int[NUM_INFUSIONS], false, false, 0, 0, 0);

    public static final Codec<CreatureInfusionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.listOf().fieldOf("infusions").forGetter(data -> toList(data.infusions())),
        Codec.BOOL.optionalFieldOf("chameleon", false).forGetter(CreatureInfusionData::chameleon),
        Codec.BOOL.optionalFieldOf("climb", false).forGetter(CreatureInfusionData::climb),
        Codec.INT.optionalFieldOf("tumor_warp", 0).forGetter(CreatureInfusionData::tumorWarp),
        Codec.INT.optionalFieldOf("tumor_warp_sticky", 0).forGetter(CreatureInfusionData::tumorWarpSticky),
        Codec.INT.optionalFieldOf("tumor_warp_temp", 0).forGetter(CreatureInfusionData::tumorWarpTemp)
    ).apply(instance, (list, chameleon, climb, tumorWarp, tumorWarpSticky, tumorWarpTemp) ->
        new CreatureInfusionData(normalize(list), chameleon, climb, tumorWarp, tumorWarpSticky, tumorWarpTemp)));

    public static final StreamCodec<ByteBuf, CreatureInfusionData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CreatureInfusionData decode(ByteBuf buffer) {
            int[] infusions = new int[NUM_INFUSIONS];
            for (int index = 0; index < NUM_INFUSIONS; index++) {
                infusions[index] = buffer.readInt();
            }
            boolean chameleon = buffer.readBoolean();
            boolean climb = buffer.readBoolean();
            int tumorWarp = buffer.readInt();
            int tumorWarpSticky = buffer.readInt();
            int tumorWarpTemp = buffer.readInt();
            return new CreatureInfusionData(infusions, chameleon, climb, tumorWarp, tumorWarpSticky, tumorWarpTemp);
        }

        @Override
        public void encode(ByteBuf buffer, CreatureInfusionData data) {
            for (int index = 0; index < NUM_INFUSIONS; index++) {
                buffer.writeInt(data.infusions()[index]);
            }
            buffer.writeBoolean(data.chameleon());
            buffer.writeBoolean(data.climb());
            buffer.writeInt(data.tumorWarp());
            buffer.writeInt(data.tumorWarpSticky());
            buffer.writeInt(data.tumorWarpTemp());
        }
    };

    public CreatureInfusionData {
        infusions = normalizeArray(infusions);
    }

    public int infusion(int slot) {
        return slot >= 0 && slot < NUM_INFUSIONS ? infusions[slot] : 0;
    }

    public CreatureInfusionData withInfusion(int slot, int recipeId) {
        int[] copy = infusions.clone();
        copy[Math.floorMod(slot, NUM_INFUSIONS)] = recipeId;
        return new CreatureInfusionData(copy, chameleon, climb, tumorWarp, tumorWarpSticky, tumorWarpTemp);
    }

    private static java.util.List<Integer> toList(int[] values) {
        java.util.List<Integer> list = new java.util.ArrayList<>(values.length);
        for (int value : values) {
            list.add(value);
        }
        return list;
    }

    private static int[] normalizeArray(int[] values) {
        int[] array = new int[NUM_INFUSIONS];
        System.arraycopy(values, 0, array, 0, Math.min(NUM_INFUSIONS, values.length));
        return array;
    }

    private static int[] normalize(java.util.List<Integer> values) {
        int[] array = new int[NUM_INFUSIONS];
        for (int index = 0; index < Math.min(NUM_INFUSIONS, values.size()); index++) {
            array[index] = values.get(index);
        }
        return array;
    }
}
