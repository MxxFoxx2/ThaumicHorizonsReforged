package dev.visfox.thaumichorizons.lib;

import net.minecraft.world.entity.Entity;

public final class EntityPlacement {
    private EntityPlacement() {
    }

    public static void snapTo(Entity entity, double x, double y, double z, float yRot, float xRot) {
        entity.setPos(x, y, z);
        entity.setYRot(yRot);
        entity.setXRot(xRot);
    }
}
