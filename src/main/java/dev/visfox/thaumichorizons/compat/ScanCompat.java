package dev.visfox.thaumichorizons.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface ScanCompat {
    default boolean isAvailable() {
        return false;
    }

    default CompatAspectList resolveEntity(Entity entity) {
        throw new UnsupportedOperationException();
    }

    default boolean handleScanPhenomena(Player player, String phenomena) {
        throw new UnsupportedOperationException();
    }
}
