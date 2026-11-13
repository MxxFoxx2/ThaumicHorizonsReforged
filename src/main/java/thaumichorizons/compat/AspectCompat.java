package thaumichorizons.compat;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface AspectCompat {
    default boolean isAvailable() {
        return false;
    }

    default void registerAspect(String tag, int color, List<String> componentTags, int blend, Identifier texture) {
        throw new UnsupportedOperationException();
    }

    default void registerAspectData(Consumer<AspectDataRegistrar> registrar) {
        throw new UnsupportedOperationException();
    }

    default CompatAspectList getObjectAspects(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    public interface AspectDataRegistrar {
        void item(ItemLike item, CompatAspectList aspects);

        void entity(EntityType<?> type, CompatAspectList aspects, Object... nbtConditions);
    }
}
