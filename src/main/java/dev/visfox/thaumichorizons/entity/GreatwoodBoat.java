package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

public final class GreatwoodBoat extends Boat {
    public GreatwoodBoat(EntityType<GreatwoodBoat> type, Level level) {
        super(type, level);
    }

    @Override
    public net.minecraft.world.item.Item getDropItem() {
        return ThItems.BOAT_GREATWOOD.get();
    }
}
