package thaumichorizons.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface GolemCompat {
    default boolean isAvailable() {
        return false;
    }

    default void registerDeathCallback(DeathCallback callback) {
        throw new UnsupportedOperationException();
    }

    default ItemStack createBellItemStack() {
        throw new UnsupportedOperationException();
    }

    default ItemStack createPlacerItemStack() {
        throw new UnsupportedOperationException();
    }

    public interface DeathCallback {
        void onGolemDeath(Entity golem);
    }
}
