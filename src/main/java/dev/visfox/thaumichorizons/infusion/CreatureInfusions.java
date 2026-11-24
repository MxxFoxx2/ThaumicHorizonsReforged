package dev.visfox.thaumichorizons.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.visfox.thaumichorizons.platform.DataKey;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;

public final class CreatureInfusions {
    public static final int NUM_INFUSIONS = 12;

    public record Data(int[] infusions, @Nullable String owner, boolean sitting) {
        public static final Data EMPTY = new Data(new int[NUM_INFUSIONS], null, false);

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.listOf().fieldOf("infusions").forGetter(data -> toList(data.infusions())),
            Codec.STRING.optionalFieldOf("owner", "").forGetter(data -> data.owner() == null ? "" : data.owner()),
            Codec.BOOL.optionalFieldOf("sitting", false).forGetter(Data::sitting)
        ).apply(instance, (list, owner, sitting) ->
            new Data(normalize(list), owner.isEmpty() ? null : owner, sitting)));

        private static List<Integer> toList(int[] values) {
            List<Integer> list = new ArrayList<>(values.length);
            for (int value : values) {
                list.add(value);
            }
            return list;
        }

        private static int[] normalize(List<Integer> values) {
            int[] array = new int[NUM_INFUSIONS];
            for (int index = 0; index < Math.min(NUM_INFUSIONS, values.size()); ++index) {
                array[index] = values.get(index);
            }
            return array;
        }
    }

    public static final DataKey<Data> KEY = DataKey.builder("creature_infusions", () -> Data.EMPTY)
        .codec(Data.CODEC)
        .build();

    private CreatureInfusions() {
    }

    public static void init() {
    }

    public static Data get(Entity entity) {
        return KEY.get(entity);
    }

    public static void set(Entity entity, Data data) {
        KEY.set(entity, data);
    }

    public static boolean hasInfusion(Entity entity, int id) {
        for (int infusion : get(entity).infusions()) {
            if (infusion == id) {
                return true;
            }
        }
        return false;
    }

    public static void addInfusion(Entity entity, int id) {
        if (id == 0) {
            return;
        }
        Data data = get(entity);
        int[] infusions = data.infusions().clone();
        for (int slot = 0; slot < infusions.length; ++slot) {
            if (infusions[slot] == 0) {
                infusions[slot] = id;
                break;
            }
        }
        set(entity, new Data(infusions, data.owner(), data.sitting()));
    }

    public static @Nullable String owner(Entity entity) {
        return get(entity).owner();
    }

    public static void setOwner(Entity entity, @Nullable String owner) {
        Data data = get(entity);
        set(entity, new Data(data.infusions(), owner, data.sitting()));
    }

    public static boolean sitting(Entity entity) {
        return get(entity).sitting();
    }

    public static void setSitting(Entity entity, boolean sitting) {
        Data data = get(entity);
        set(entity, new Data(data.infusions(), data.owner(), sitting));
    }
}
